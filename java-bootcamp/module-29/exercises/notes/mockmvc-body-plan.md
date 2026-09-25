# Lab 29 - MockMvc body plan

| Case                       | HTTP | Body assertions                                                             |
|----------------------------|-----:|-----------------------------------------------------------------------------|
| Blank fields and bad email |  400 | timestamp, status, reason, safe message, correlation, four violation fields |
| Missing `CUS-9999`         |  404 | status, Not Found, safe message, correlation, empty violations              |
| Duplicate `CUS-1001`       |  409 | status, Conflict, safe message, correlation                                 |
| GET Amina and Ravi         |  200 | names and ACTIVE/PROSPECT statuses                                          |
| No Bearer                  |  401 | security remains enforced                                                   |

Log in first for secured customer cases. Assert violation fields without depending on incidental order; the handler also
sorts them. Status-only tests would miss client-breaking envelope changes. The supplied DTO has `error`, not `code`.
