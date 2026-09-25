# Lab 28 - Authentication versus authorization

| Concept        | Question                 | CRM case                                       | Result |
|----------------|--------------------------|------------------------------------------------|--------|
| Authentication | Who is calling?          | No or invalid Bearer token on customer GET     | 401    |
| Authorization  | What may this caller do? | Authenticated `agent1` calls `/api/admin/ping` | 403    |

`agent1` can read Amina (`CUS-1001`, ACTIVE); `admin1` can use the admin route. An expired token would be a 401 in a
real JWT system, but the lab stub has no expiration. `lab-request-001` is a correlation ID, not authentication.
