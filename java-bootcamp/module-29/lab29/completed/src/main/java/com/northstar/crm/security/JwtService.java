package com.northstar.crm.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final String signature;

  public JwtService(@Value("${northstar.security.jwt-secret}") String secret) {
    this.signature = Integer.toHexString(secret.hashCode());
  }

  // Finished prompt: implement the course's lab stub, not a production JWT.
  public String issueToken(String subject, String role) {
    if (!validSubject(subject) || !validRole(role)) {
      throw new IllegalArgumentException("Invalid token claims");
    }
    return "lab." + subject + "." + role + "." + signature;
  }

  public String parseSubject(String token) {
    return parts(token)[1];
  }

  public String parseRole(String token) {
    return parts(token)[2];
  }

  private String[] parts(String token) {
    String[] parts = token == null ? new String[0] : token.split("\\.", -1);
    if (parts.length != 4 || !"lab".equals(parts[0]) || !validSubject(parts[1])
        || !validRole(parts[2]) || !signature.equals(parts[3])) {
      throw new IllegalArgumentException("Invalid lab token");
    }
    return parts;
  }

  private boolean validSubject(String subject) {
    return subject != null && subject.matches("[A-Za-z0-9_-]+");
  }

  private boolean validRole(String role) {
    return "AGENT".equals(role) || "ADMIN".equals(role);
  }
}
