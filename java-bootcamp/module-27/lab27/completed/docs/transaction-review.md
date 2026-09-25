# Lab 27 transaction design review

I reviewed the starter with Codex assistance and verified the finished behavior with integration tests. This is a record
of my design choices, not a claim of separate human sign-off.

| Decision                                    | Reason                                                                                     |
|---------------------------------------------|--------------------------------------------------------------------------------------------|
| Public `@Transactional` service method      | The controller calls through Spring's proxy; debit, credit, and log share one transaction. |
| Flush source debit before failure           | Demonstrates rollback of a real SQL update. Flush is not commit.                           |
| No `REQUIRES_NEW` for either account or log | Independent commits could leave a permanent partial transfer.                              |
| Let unchecked failure escape                | Spring can roll back instead of committing after a swallowed exception.                    |
| Check balances and log after failure        | An exception alone does not prove ledger atomicity.                                        |

This is still a teaching workflow. I did not test concurrent transfers, lost updates, idempotency, or restart
durability. A real payment service would need those guarantees and a persistent database.
