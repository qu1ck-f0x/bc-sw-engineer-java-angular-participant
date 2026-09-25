# Module 28 lessons learned

| Part                                | Significance                                                           | What I should learn                                                                                 |
|-------------------------------------|------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| Authentication versus authorization | Separates identity from permission.                                    | No/invalid token is 401; a known AGENT denied admin access is 403.                                  |
| Filter chain                        | Protects routes by default.                                            | Keep login/health/error public, customers AGENT or ADMIN, admin ADMIN only, and sessions stateless. |
| Login and Bearer flow               | Connects password verification to later requests.                      | Check credentials before issuing a token; rebuild the context on each request.                      |
| MockMvc matrix                      | Makes security regressions visible.                                    | Test both successful and denied paths, including malformed tokens.                                  |
| Production checklist                | Keeps a lab demonstration from being mistaken for deployable security. | The hash-based stub has no real signature or expiry; production needs an IdP and managed keys.      |
| Readiness and lab                   | Proves the full route flow.                                            | Check 401/403 on live Tomcat as well as MockMvc and never commit secrets or tokens.                 |
