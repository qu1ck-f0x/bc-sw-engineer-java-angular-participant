package com.northstar.crm.controller;

import com.northstar.crm.security.CrmUserDetailsService;
import com.northstar.crm.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final JwtService jwtService;
  private final CrmUserDetailsService users;
  private final PasswordEncoder passwordEncoder;

  public AuthController(JwtService jwtService, CrmUserDetailsService users, PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.users = users;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public Map<String, String> login(@RequestBody Map<String, String> body) {
    try {
      UserDetails user = users.loadUserByUsername(body.get("username"));
      if (body.get("password") != null && passwordEncoder.matches(body.get("password"), user.getPassword())) {
        String role = user.getAuthorities().iterator().next().getAuthority().substring("ROLE_".length());
        // Finished prompt: issue a token only after checking credentials.
        return Map.of("accessToken", jwtService.issueToken(user.getUsername(), role), "tokenType", "Bearer");
      }
    } catch (UsernameNotFoundException ignored) {
      // Unknown users and wrong passwords share one response.
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
  }
}
