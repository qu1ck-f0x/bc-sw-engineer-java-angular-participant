# Lab 15 — service layer notes

## Status transition table

| From      | Allowed to        |
|-----------|-------------------|
| PROSPECT  | ACTIVE, CLOSED    |
| ACTIVE    | SUSPENDED, CLOSED |
| SUSPENDED | ACTIVE, CLOSED    |
| CLOSED    | (none)            |

## Wiring

- Shared `InMemoryCustomerRepository` instance for `CustomerValidator` + `DefaultCustomerService`
- No `HashMap` / JDBC / `EntityManager` in the `service` package

## Smoke demo result

- Ravi starts as `PROSPECT` and is changed to `ACTIVE` through `CustomerService.changeStatus`.
- Amina starts as `ACTIVE`. The attempted `ACTIVE -> PROSPECT` change is rejected before the entity is mutated.
- The failure message includes `lab-request-001`, so the console output can be traced back to the request that caused
  it.

## What I changed

- `InMemoryCustomerRepository` keeps the `HashMap` private and exposes only repository methods.
- `CustomerValidator` owns the business rules for duplicate customers and allowed status transitions.
- `DefaultCustomerService` depends on the `CustomerRepository` interface and validates before changing status.
- `Main` wires one shared repository into both the validator and service, then proves the legal and illegal status
  paths.
- `CustomerValidatorTest` covers the legal transition, illegal transition, and duplicate customer ID path.

## What I learned

The big idea is that service code should coordinate use cases and protect business rules, while repository code should
only handle storage. The most important ordering detail is validate-before-mutate: if I set Amina to `PROSPECT` before
checking the rule, the failed operation would still corrupt the in-memory customer.
