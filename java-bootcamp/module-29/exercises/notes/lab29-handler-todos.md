# Lab 29 - Handler plan

`@RestControllerAdvice` under `com.northstar.crm.api` handles MVC failures centrally. Map
`MethodArgumentNotValidException` and malformed JSON to 400, the starter service's `IllegalArgumentException` to 404,
and `IllegalStateException` to 409. Preserve bad-login `ResponseStatusException` as 401. The final generic `Exception`
handler logs internally but returns a 500 envelope with only `Unexpected error`.

Specific handlers take precedence over the fallback. The response helper fills the existing `ErrorResponse` fields and
the `X-Correlation-Id` value (or lab default). There is no custom `CustomerNotFoundException` in this starter.
