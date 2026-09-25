# Lab 29 - CRM error contract

## Contract and security

Customer routes require a Bearer token from the Lab 28 login. The supplied `ErrorResponse` has `timestamp`, numeric
`status`, HTTP reason `error`, safe `message`, `correlationId`, and `violations[{field, message}]`. There is no `code`,
`path`, or `rejectedValue` property. The handler always sets a violations list, empty when no field error exists. It
echoes `X-Correlation-Id`, using `lab-request-001` when the header is absent or blank.

| Case                      | HTTP | Error / safe message                     | Violations          |
|---------------------------|-----:|------------------------------------------|---------------------|
| Invalid `CustomerRequest` |  400 | Bad Request / Validation failed          | Sorted field errors |
| Malformed JSON            |  400 | Bad Request / Malformed request body     | Empty               |
| Missing `CUS-9999`        |  404 | Not Found / Customer not found           | Empty               |
| Duplicate `CUS-1001`      |  409 | Conflict / Duplicate customer            | Empty               |
| Wrong login               |  401 | Unauthorized / Invalid credentials       | Empty               |
| Unexpected exception      |  500 | Internal Server Error / Unexpected error | Empty               |

The security filter can reject a request before MVC advice runs; an anonymous customer request therefore gets a 401
status without claiming the MVC envelope. The fallback logs details server-side but does not send exception text, SQL,
stack traces, passwords, or tokens to clients.

## Reproduce

From `module-29/lab29/completed`, run `mvn -B test` or `mvn spring-boot:run`. Keep the classroom token in a shell
variable, not a file:

```powershell
$base = 'http://localhost:8080'
$token = (Invoke-RestMethod -Method Post -Uri "$base/api/auth/login" -ContentType 'application/json' -Body '{"username":"agent1","password":"agent1"}').accessToken
$headers = @{Authorization="Bearer $token"; 'X-Correlation-Id'='lab-request-001'}
Invoke-WebRequest -Method Post -Uri "$base/api/customers" -Headers $headers -ContentType 'application/json' -Body '{"id":"","name":"","email":"bad","status":""}' -SkipHttpErrorCheck
Invoke-WebRequest -Uri "$base/api/customers/CUS-9999" -Headers $headers -SkipHttpErrorCheck
```

Stop the server with Ctrl+C. Do not commit a real secret, a raw token, or `target/`.

## Evidence and failure experiments

The final `ErrorEnvelopeTest` run passed **4 tests, 0 failures, 0 errors**. It checks 400 validation and malformed JSON,
404, 409, bad-login 401, anonymous 401, successful reads of Amina and Ravi, and a valid create returning 201. A direct
handler check confirms that the fallback body says `Unexpected error` without exposing the internal exception text; it
is not a live 500 test.

The packaged app was run on a temporary local port. Observed responses were anonymous customer **401**, `GET CUS-1001`
**200**, invalid POST **400** with four violations and `lab-request-001`, missing `CUS-9999` **404**, and duplicate
`CUS-1001` **409**. The server was stopped afterward. I did not remove `@Valid` or force an unhandled live exception
because those experiments would alter the working app.

| Experiment                                   | Observation                | Lesson                                           |
|----------------------------------------------|----------------------------|--------------------------------------------------|
| Blank fields and malformed email with Bearer | 400, four field violations | DTO constraints run through controller `@Valid`. |
| Malformed JSON with Bearer                   | 400, safe envelope         | Parsing failures need a separate mapping.        |
| Unknown ID and duplicate                     | 404 and 409 envelopes      | Expected service failures should not become 500. |
| Omit Bearer                                  | 401                        | Security runs before controller validation.      |

Lab 14's request DTO idea keeps client input separate from the model; Lab 16's centralized handler gives controllers one
error policy. This lab joins them into a stable Spring Boot contract that Angular can render through `message` and
`violations`.

## Checkpoints and reflection

Module-local completed code, the existing `ErrorResponse` shape, DTO constraints, controller `@Valid`, 400/404/409
mappings, safe fallback, four tests, secured happy paths, and secret/build hygiene are all **Pass**. `@Valid` at the
HTTP boundary was the most important correctness decision. Body assertions prove more than status-only checks, and
forgetting Bearer is the easiest way to confuse a security 401 with a validation failure.
