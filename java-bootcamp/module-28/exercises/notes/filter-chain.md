# Lab 28 - Security filter chain

| Route                                           | Rule           |
|-------------------------------------------------|----------------|
| `/api/auth/login`, `/actuator/health`, `/error` | permitAll      |
| `OPTIONS /**`                                   | permitAll      |
| `/api/customers/**`                             | AGENT or ADMIN |
| `/api/admin/**`                                 | ADMIN only     |
| Other paths                                     | authenticated  |

Use STATELESS sessions, disable form login and HTTP Basic, and place the lab token filter before
`UsernamePasswordAuthenticationFilter`. CSRF is disabled for this Bearer-only API because it does not rely on
automatically sent browser cookies. Protecting `/error` could mask an actual 403 as 401 on live Tomcat. Making customers
permitAll would expose customer data.
