# Module 27 lessons learned

| Part                 | Significance                                     | What I should learn                                                                        |
|----------------------|--------------------------------------------------|--------------------------------------------------------------------------------------------|
| ACID                 | Gives precise language for database correctness. | Measure atomicity and consistency; do not claim untested isolation or restart durability.  |
| Transaction boundary | Defines the whole transfer as one unit of work.  | Use a public service method called through Spring's proxy.                                 |
| Rollback plan        | Shows whether partial writes survive.            | Throw after a flushed debit, then read both balances and the log.                          |
| Transfer pseudocode  | Makes operation order explicit.                  | Validate, debit, possibly fail, credit, and log in one transaction.                        |
| Propagation warnings | Explains partial-commit hazards.                 | Avoid separate `REQUIRES_NEW` operations, swallowed failures, and self-invocation.         |
| Readiness and lab    | Turns the design into evidence.                  | Reset fixtures, test success and failure, and distinguish HTTP status from database proof. |
