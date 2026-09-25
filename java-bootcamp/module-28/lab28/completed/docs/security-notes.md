# Lab 28 - Security notes

## Flow and route rules

`POST /api/auth/login` validates the classroom user's BCrypt password and returns `accessToken` plus
`tokenType: Bearer`. The client sends the token in the Authorization header on each protected request. The filter parses
the lab stub, reloads the named user, checks the role against that user's actual authority, and sets the Spring Security
context. No session is created.

| Route                            | Rule           |
|----------------------------------|----------------|
| Login, actuator health, `/error` | permitAll      |
| `OPTIONS /**`                    | permitAll      |
| `/api/customers/**`              | AGENT or ADMIN |
| `/api/admin/**`                  | ADMIN only     |
| Other requests                   | authenticated  |

`agent1`/`agent1` has AGENT; `admin1`/`admin1` has ADMIN. Amina (`CUS-1001`) is ACTIVE, and Ravi (`CUS-1002`) is
PROSPECT. `lab-request-001` is a correlation header, not a credential. **401** means missing or invalid authentication;
**403** means an authenticated user lacks the role. Public `/error` prevents a live Tomcat error dispatch from masking
403 as 401.

## Reproduce without saving a token

From `module-28/lab28/completed`, run `mvn -B test` or `mvn spring-boot:run`. This PowerShell example holds the lab
token only in memory:

```powershell
$base = 'http://localhost:8080'
$agent = (Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType 'application/json' -Body '{"username":"agent1","password":"agent1"}').accessToken
Invoke-RestMethod -Uri "$base/api/customers/CUS-1001" -Headers @{Authorization="Bearer $agent"; 'X-Correlation-Id'='lab-request-001'}
Invoke-WebRequest -Uri "$base/api/admin/ping" -Headers @{Authorization="Bearer $agent"} -SkipHttpErrorCheck | Select-Object StatusCode
```

Stop the server with Ctrl+C. `.env.example` contains only a classroom placeholder, `.env` is ignored, and no real
`JWT_SECRET` or raw token belongs in Git, screenshots, or logs.

## Evidence and failure experiments

The final MockMvc suite passed **3 tests with no failures or errors**. It checks missing/bad Bearer, wrong login, AGENT
customer 200, AGENT admin 403, ADMIN admin 200, and rejects altered role or unknown subject claims. The packaged app was
also run on a temporary port. Live results were anonymous customer **401**, agent customer **200**, agent admin **403**,
admin admin **200**, and malformed Bearer **401**. I stopped the temporary server.

| Experiment                         | Observation                                                     |
|------------------------------------|-----------------------------------------------------------------|
| Wrong password or malformed Bearer | 401; no access token issued for bad login                       |
| AGENT calls admin                  | 403 on live Tomcat                                              |
| Modify role or subject in the stub | MockMvc rejects with 401                                        |
| Different issuer/verifier secrets  | Expected 401, but not run as a separate live configuration test |

## Production migration

The `lab.<subject>.<role>.<hex(secret.hashCode())>` token is **not HS256 or a real JWT**. Its hash is forgeable, it has
no expiration, and the default lab secret is public. It must not be exposed as production security. Replace it and the
hardcoded users with an enterprise OAuth2/OIDC IdP and standards-based resource server. Verify signature, issuer,
audience, expiry, and key ID using managed JWKS keys; keep keys in a secret manager with rotation and incident
procedures. Use HTTPS, short token lifetimes, least-privilege roles, reviewed admin grants, login monitoring, and logs
without raw passwords or Bearer tokens.

## Checkpoints and reflection

The module-local project, placeholder-only secret config, stateless chain, login, Bearer role matrix, three tests, and
production checklist are all **Pass**. Stateless authentication made every request's identity explicit. The clearest
proof of authorization is one AGENT token getting 200 for a customer but 403 for admin while ADMIN gets 200. The
easy-to-miss behavior was `/error` dispatch on live Tomcat.
