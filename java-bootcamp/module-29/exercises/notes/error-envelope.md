# Lab 29 - ErrorResponse envelope

The supplied class has `timestamp`, numeric `status`, HTTP reason `error`, safe `message`, `correlationId`, and
`violations`. Each violation has only `field` and `message`. The exercise's illustrative `code` field is not in this
lab's class; I did not redesign it or add `path` or `rejectedValue`.

Echo a supplied `X-Correlation-Id`, such as `lab-request-001`; use that lab value when the header is missing or blank.
Do not send rejected passwords, raw tokens, SQL text, or stack traces. A plain string body would not give Angular a
stable contract.
