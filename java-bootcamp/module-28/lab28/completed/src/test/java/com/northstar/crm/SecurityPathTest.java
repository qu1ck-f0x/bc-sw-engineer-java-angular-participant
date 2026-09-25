package com.northstar.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityPathTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  @Test
  void missingTokenIs401() throws Exception {
    // Finished prompt: missing, malformed, and forged Bearer credentials remain anonymous.
    mvc.perform(get("/api/customers/CUS-1001")).andExpect(status().isUnauthorized());
    mvc.perform(get("/api/customers/CUS-1001").header("Authorization", "Bearer garbage"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"agent1\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"unknown\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void agentCanReadCustomerButNotAdmin() throws Exception {
    String token = login("agent1", "agent1");
    mvc.perform(get("/api/customers/CUS-1001").header("Authorization", "Bearer " + token)
        .header("X-Correlation-Id", "lab-request-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("CUS-1001"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
    // Finished prompt: an authenticated AGENT is forbidden from the ADMIN route.
    mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token.replace(".AGENT.", ".ADMIN.")))
        .andExpect(status().isUnauthorized());
    mvc.perform(get("/api/customers/CUS-1001").header("Authorization", "Bearer " + token.replace(".agent1.", ".unknown.")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void adminCanPing() throws Exception {
    String token = login("admin1", "admin1");
    mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("ADMIN"));
    mvc.perform(get("/api/customers/CUS-1002").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PROSPECT"));
  }

  private String login(String username, String password) throws Exception {
    String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content(json.writeValueAsString(new LoginRequest(username, password))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andReturn().getResponse().getContentAsString();
    String token = json.readTree(body).get("accessToken").asText();
    assertTrue(token.startsWith("lab."));
    return token;
  }

  private record LoginRequest(String username, String password) {}
}
