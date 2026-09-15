# Lab 16 starter — timed path (~45 minutes)

**Theme:** API exception handling — ErrorResponse + GlobalExceptionHandler + correlation

## Activity card

|                      |                                                                                      |
|----------------------|--------------------------------------------------------------------------------------|
| **Objective**        | Implement ErrorResponse, BusinessException factories, handler, and 400/404/409 demos |
| **Skills practiced** | Catch order, Fail JSON, correlationId, safe messages                                 |
| **Expected outcome** | `mvn -B clean test` green · Fail JSON includes `lab-request-001`                     |
| **Estimated time**   | ~45 minutes                                                                          |
| **Files**            | `module-16/lab16/starter/` in this workspace                                         |

**Boilerplate reduced:** Baseline CRM + TODOs given — fill `// TODO`; catch BusinessException **before** Exception.

Pacing: [`../../PACING.md`](../../PACING.md) · Full steps: [`../LAB-16-GUIDE.md`](../LAB-16-GUIDE.md) · Codes: [
`../../HTTP-STATUS-CODES.md`](../../HTTP-STATUS-CODES.md)

**Honesty:** Prefer **409** for illegal transitions (document 422 if chosen). No stack traces to clients.

## Workspace location

Keep this lab work inside the module directory. Use `module-16/lab16/starter/` while the lab is still a starter, then
create `module-16/lab16/completed/` when the implementation is finished.

**Windows (PowerShell)** — from the repository root:

```powershell
cd module-16\lab16\starter
```

**macOS / Linux:**

```bash
cd module-16/lab16/starter
```

Full GUIDE: [`../LAB-16-GUIDE.md`](../LAB-16-GUIDE.md)

## 45-minute checklist

- [ ] Implement `ErrorResponse` (+ toJson always includes errors)
- [ ] Complete `BusinessException.notFound` / `conflict` factories
- [ ] Implement `GlobalExceptionHandler` (business / validation / unexpected)
- [ ] Wire facade create/get/changeStatus to return `ApiResult` (catch BusinessException before Exception)
- [ ] Refactor validator/service to throw BusinessException; demo 400/404/409 in Main
- [ ] Finish `GlobalExceptionHandlerTest`; run smoke test

## Smoke test

```bash
mvn -B clean test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-16/` (redact secrets).

## Timed-path Pass criteria

| Criterion                                                  | Pass / Fail |
|------------------------------------------------------------|-------------|
| Handler tests green (404 / 409 / generic 500)              | Pass / Fail |
| Facade Fail JSON includes correlationId lab-request-001    | Pass / Fail |
| 400 validation / 404 not-found / 409 conflict demonstrated | Pass / Fail |
| Illegal transition leaves CUS-1001 ACTIVE                  | Pass / Fail |

Continue remaining GUIDE steps as homework / full path if needed.
