# Lab 28 - MockMvc matrix

| Case                 | Route                 | Expected    |
|----------------------|-----------------------|-------------|
| No token             | GET `CUS-1001`        | 401         |
| Malformed Bearer     | GET `CUS-1001`        | 401         |
| Wrong login password | POST login            | 401         |
| AGENT Bearer         | GET `CUS-1001`        | 200, ACTIVE |
| AGENT Bearer         | GET `/api/admin/ping` | 403         |
| ADMIN Bearer         | GET `/api/admin/ping` | 200         |

The tests obtain tokens by logging in first. A bad token is checked separately from no token so the parser and filter
behavior are covered. Status-only checks are sufficient for denials, while success paths also assert returned data.
