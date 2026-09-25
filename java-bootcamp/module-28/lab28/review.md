# Module 28 lab review

## 1. Restore the module-local work

I checked the saved workflow and found only starter copies in the current workspace, so I copied the supplied lab into
`lab28/completed` and exercise templates into `exercises/notes`. I kept all edits in `java-bootcamp/module-28` and left
the course README and guides unchanged. I used the timed-path lab stub because the guide makes real HS256 an optional
extension, not the core exercise.

## 2. Complete exercises 1 through 6

I filled the authentication/authorization comparison, filter-chain route sketch, login/Bearer flow, MockMvc matrix,
production IdP checklist, and readiness checklist. I used the given `agent1`/AGENT, `admin1`/ADMIN, Amina `CUS-1001`
/ACTIVE, and Ravi `CUS-1002`/PROSPECT fixtures. My central rule was 401 for missing or bad credentials and 403 for an
authenticated agent lacking admin rights.

## 3. Implement the security chain

The starter had a temporary HTTP Basic configuration and TODO matchers. I made requests stateless, disabled form login
and HTTP Basic, placed `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`, and added public
login/health/error, customer roles, and admin-only rules. The modified matcher block is:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/login", "/actuator/health", "/error").permitAll()
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/customers/**").hasAnyRole("AGENT", "ADMIN")
    .anyRequest().authenticated())
```

I left `/error` public because live Tomcat can otherwise show 401 in place of the intended 403. The service uses the
existing `CrmUserDetailsService` and BCrypt encoder, not a second user store.

## 4. Issue and parse the lab token

I implemented the guide's `lab.<subject>.<role>.<hex(secret.hashCode())>` format in `JwtService`. The parser requires
exactly four parts, the `lab` prefix, a supported role, and the expected signature fragment. It rejects malformed input
instead of accepting any string that begins with `lab`:

```java
if (parts.length != 4 || !"lab".equals(parts[0]) || !validSubject(parts[1])
    || !validRole(parts[2]) || !signature.equals(parts[3])) {
  throw new IllegalArgumentException("Invalid lab token");
}
```

This satisfies the classroom contract but does not provide cryptographic security or expiry. I did not call it a
production JWT.

## 5. Fix login and Bearer authentication

The starter login issued a token based on a username prefix without checking the password. I changed it to load a real
lab user, check the submitted password with `PasswordEncoder.matches`, derive the role from that user, and return the
token only on success. Unknown users and wrong passwords both return 401. The filter parses the token, reloads the user,
verifies the claimed role against that user's actual authorities, and fills the security context:

```java
if (user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role))) {
  SecurityContextHolder.getContext().setAuthentication(
      new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
}
```

That extra user lookup means simply editing `.AGENT.` to `.ADMIN.` in an agent token does not grant admin access, even
within this teaching stub.

## 6. Replace placeholder tests and verify live behavior

I replaced the three `fail` placeholders with MockMvc tests. They cover anonymous and malformed-token 401, bad-login
401, agent customer 200, agent admin 403, admin admin 200, and altered claims 401. The login helper reads the response
token in memory rather than printing it. `mvn -B test` passed **3 tests, 0 failures, 0 errors**; packaging also passed.

I ran the packaged app on a temporary local port and observed anonymous customer **401**, agent customer **200**, agent
admin **403**, admin admin **200**, and malformed Bearer **401**. I stopped it afterward. The live 403 check mattered
because `/error` dispatch can differ from MockMvc behavior.

## 7. Document limits and clean up

I completed `completed/docs/security-notes.md` with the route matrix, a token-safe local runbook, observed results,
failure experiments, and a production IdP/key-rotation checklist. I noted that `hashCode` is forgeable, the stub never
expires, and the default secret is for class only. I scanned for unfinished prompts, kept helpful ones as
`Finished prompt` comments, removed starter copies and build output after verification, and left the final lab and notes
in `module-28`.
