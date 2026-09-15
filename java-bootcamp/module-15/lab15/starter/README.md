# Lab 15 starter — timed path (~45 minutes)

**Theme:** Service layer + CustomerValidator status transitions (no persistence leak)

## Activity card

|                      |                                                                                              |
|----------------------|----------------------------------------------------------------------------------------------|
| **Objective**        | Implement repo, validator ALLOWED matrix, DefaultCustomerService, and demos                  |
| **Skills practiced** | Ctor DI, validate-before-mutate, illegal-transition proof                                    |
| **Expected outcome** | `mvn -B clean test` green · activate CUS-1002 · reject ACTIVE→PROSPECT                       |
| **Estimated time**   | ~45 minutes                                                                                  |
| **Files**            | Starter source in `module-15/lab15/starter/`; completed copy in `module-15/lab15/completed/` |

**Boilerplate reduced:** Baseline CRM completed from the starter; keep Map out of `service`.

Pacing: [`../../PACING.md`](../../PACING.md) · Full steps: [`../LAB-15-GUIDE.md`](../LAB-15-GUIDE.md)

**Honesty:** No `@ControllerAdvice` / Spring MVC error mapping in Lab 15 (Lab 16).

## Workspace location

Keep this lab work inside the module directory. The completed version is in `module-15/lab15/completed/`, and the full
guide remains one folder up at [`../LAB-15-GUIDE.md`](../LAB-15-GUIDE.md).

## 45-minute checklist

- [x] Implement `InMemoryCustomerRepository` (private Map)
- [x] Fill `CustomerValidator` ALLOWED transitions + validateNew / validateTransition
- [x] Implement `DefaultCustomerService` constructor DI + changeStatus (validate before mutate)
- [x] Main: activate CUS-1002; reject ACTIVE→PROSPECT on CUS-1001; prove status unchanged
- [x] Finish `CustomerValidatorTest`; fill `docs/service-layer-notes.md`
- [x] Run smoke test

## Smoke test

```bash
mvn -B clean test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-15/` (redact secrets).

## Timed-path Pass criteria

| Criterion                                                                   | Pass / Fail |
|-----------------------------------------------------------------------------|-------------|
| Validator tests green (legal + illegal + duplicate)                         | Pass        |
| Main activates CUS-1002 to ACTIVE                                           | Pass        |
| Illegal transition leaves CUS-1001 ACTIVE; message includes lab-request-001 | Pass        |
| No HashMap / JDBC in service package                                        | Pass        |

Continue remaining GUIDE steps as homework / full path if needed.
