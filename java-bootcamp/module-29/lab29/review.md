# Module 29 lab review

## 1. Restore and inspect the module

The current workspace had starter copies but no completed Module 29 work, so I copied the starter to `lab29/completed`
and note templates to `exercises/notes`, all under `java-bootcamp/module-29`. I read the guide and starter first. I kept
the supplied `ErrorResponse` unchanged; it has `error` but no `code`, and the request DTO has `name` rather than the
exercise example's `fullName`. I followed those real contracts in my answers. I left the course README and guides alone.

## 2. Complete the six exercises

I filled the DTO constraints, handler plan, envelope fields, exception/status map, MockMvc body plan, and readiness
check. The guide's order is 1, 2, 3, 4, 6, 5, so I finished the test plan before marking readiness Pass. I chose
`@NotBlank` for ID/name/email/status and `@Email` for email. I kept duplicate ID detection in the service, not in Bean
Validation. My body plan asserts both status and client-facing JSON fields with Bearer authentication.

## 3. Carry forward the verified security baseline

The starter included Lab 28 security, but its older filter trusted the token role claim directly. I copied the tested
Module 28 `SecurityConfig`, `JwtService`, filter, and login controller into this completed lab. The filter now reloads
the named user and checks the claim against actual authorities. This keeps the module's prerequisite behavior aligned
without changing the guide or adding a new authentication system. The token remains an intentionally insecure classroom
stub, not production JWT.

## 4. Validate at the controller boundary

I added Jakarta validation annotations to `CustomerRequest` and `@Valid` to `CustomerController.create`. The relevant
modified lines are:

```java
@NotBlank
private String name;
@NotBlank
@Email
private String email;

public Customer create(
    @Valid @RequestBody CustomerRequest request,
    @RequestHeader(value = "X-Correlation-Id", defaultValue = "lab-request-001") String correlationId)
```

The ID and status also have `@NotBlank`. Without controller `@Valid`, annotations would be inert. The service still
detects duplicates and throws `IllegalStateException` for `CUS-1001`.

## 5. Build a stable error handler

I completed `GlobalExceptionHandler` under `com.northstar.crm.api`, which Spring scans. For validation, it maps field
errors to the supplied two-field `FieldViolation` and sorts them for stable output:

```java
List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
    .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
    .sorted(Comparator.comparing(FieldViolation::getField).thenComparing(FieldViolation::getMessage))
    .toList();
return respond(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
```

The common helper fills status, HTTP reason, safe message, correlation ID, and violations. It echoes the supplied
`X-Correlation-Id` or uses `lab-request-001`. I did not add `code`, `path`, or rejected values because they are absent
from `ErrorResponse` and could expose input unnecessarily.

## 6. Map failures and protect login status

Malformed JSON maps to 400; the starter service's missing-customer `IllegalArgumentException` maps to 404; its duplicate
`IllegalStateException` maps to 409. I used fixed safe messages rather than echoing arbitrary exception text:

```java
return respond(HttpStatus.NOT_FOUND, "Customer not found", request, List.of());
return respond(HttpStatus.CONFLICT, "Duplicate customer", request, List.of());
```

I added a `ResponseStatusException` handler so bad login remains 401 instead of falling into the generic 500 handler.
The fallback logs diagnostics server-side and returns only `Unexpected error`. A security-filter 401 may be produced
before MVC advice, so I did not pretend every anonymous request gets this envelope.

## 7. Replace the four placeholder tests

I finished `ErrorEnvelopeTest` using a login helper that holds the agent token in memory. The tests check 400 with four
field violations and malformed JSON; 404 for `CUS-9999`; 409 for duplicate `CUS-1001`; anonymous and bad-login 401;
happy reads of Amina and Ravi; and valid create 201. I also invoked the fallback handler directly to check that a 500
client body does not include internal exception text. One representative body assertion is:

```java
.andExpect(status().isBadRequest())
.andExpect(jsonPath("$.message").value("Validation failed"))
.andExpect(jsonPath("$.correlationId").value("lab-request-001"))
.andExpect(jsonPath("$.violations[*].field", hasItems("id", "name", "email", "status")));
```

I asserted the violation fields as a set rather than depending on incidental order. `mvn -B test` passed **4 tests, 0
failures, 0 errors**; packaging passed too.

## 8. Check live HTTP and document the contract

I ran the jar on a temporary port and kept the token in a PowerShell variable. Live responses were anonymous **401**,
`GET CUS-1001` **200**, invalid POST **400** with four violations and the expected correlation ID, missing `CUS-9999`
**404**, and duplicate `CUS-1001` **409**. I stopped the server afterward. I did not induce a live 500; only the
fallback handler body was checked directly.

I filled `completed/docs/error-contract.md` with the actual envelope, status map, run instructions, observed failure
experiments, and the connection to Lab 14 DTOs and Lab 16 handlers. I scanned for unfinished prompts, kept useful ones
as `Finished prompt` comments, removed starter copies and generated build output after verification, and left the
finished work in `module-29`.

The in-memory service's separate duplicate check and insert is not a concurrent uniqueness guarantee. That and the
classroom token would need replacement before a real CRM deployment, but neither changes this lab's validation and
error-contract objective.
