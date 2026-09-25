package com.northstar.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.northstar.crm.api.GlobalExceptionHandler;
import com.northstar.crm.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorEnvelopeTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;
  @Autowired GlobalExceptionHandler handler;

  @Test
  void validationReturns400Envelope() throws Exception {
    String token = login();
    // Finished prompt: check the response body as well as HTTP status.
    mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .header("X-Correlation-Id", "lab-request-001")
        .content("{\"id\":\"\",\"name\":\"\",\"email\":\"bad\",\"status\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.correlationId").value("lab-request-001"))
        .andExpect(jsonPath("$.violations.length()").value(4))
        .andExpect(jsonPath("$.violations[*].field", hasItems("id", "name", "email", "status")));
    mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token).content("{invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));
  }

  @Test
  void missingCustomerReturns404Envelope() throws Exception {
    String token = login();
    mvc.perform(get("/api/customers/CUS-9999").header("Authorization", "Bearer " + token)
        .header("X-Correlation-Id", "case-404"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Customer not found"))
        .andExpect(jsonPath("$.correlationId").value("case-404"))
        .andExpect(jsonPath("$.violations.length()").value(0));
    mvc.perform(get("/api/customers/CUS-1001").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Amina Khan"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
    mvc.perform(get("/api/customers/CUS-1002").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Ravi Singh"))
        .andExpect(jsonPath("$.status").value("PROSPECT"));
    mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content("{\"id\":\"CUS-1003\",\"name\":\"Maya Chen\",\"email\":\"maya.chen@example.com\",\"status\":\"PROSPECT\"}"))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value("CUS-1003"));
  }

  @Test
  void duplicateReturns409Envelope() throws Exception {
    String token = login();
    mvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .header("X-Correlation-Id", "lab-request-001")
        .content("{\"id\":\"CUS-1001\",\"name\":\"Amina Khan\",\"email\":\"amina.khan@example.com\",\"status\":\"ACTIVE\"}"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Duplicate customer"))
        .andExpect(jsonPath("$.correlationId").value("lab-request-001"))
        .andExpect(jsonPath("$.violations.length()").value(0));
  }

  @Test
  void securityStillRequiresToken() throws Exception {
    mvc.perform(get("/api/customers/CUS-1001")).andExpect(status().isUnauthorized());
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"agent1\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Invalid credentials"));
    ResponseEntity<ErrorResponse> fallback = handler.handleSafe500(
        new RuntimeException("private internal diagnostic"), new MockHttpServletRequest());
    assertEquals(500, fallback.getStatusCode().value());
    assertNotNull(fallback.getBody());
    assertEquals("Unexpected error", fallback.getBody().getMessage());
    assertFalse(json.writeValueAsString(fallback.getBody()).contains("private internal diagnostic"));
  }

  private String login() throws Exception {
    String response = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"agent1\",\"password\":\"agent1\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andReturn().getResponse().getContentAsString();
    return json.readTree(response).get("accessToken").asText();
  }
}
