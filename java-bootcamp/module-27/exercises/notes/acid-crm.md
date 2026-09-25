# Lab 27 - ACID in the CRM transfer

| Property    | CRM meaning and evidence                                                                                                                                                   |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Atomicity   | A 10.00 transfer to `ACC-FORCE-FAIL` throws after the source debit is flushed; MAIN remains 1000.00, LOYALTY remains 50.00, and no log survives.                           |
| Consistency | A successful 5.00 transfer leaves MAIN 995.00 and LOYALTY 55.00, preserving the combined 1050.00 and writing exactly one matching log.                                     |
| Isolation   | Other transactions should not see uncommitted changes at the configured isolation level, but this lab does not run concurrent requests or prove freedom from lost updates. |
| Durability  | A committed change remains readable during the running H2 process. `jdbc:h2:mem:crmdb` is in-memory, so restart durability is not demonstrated.                            |

I used the actual seeded IDs `ACC-MAIN-1001` and `ACC-LOYALTY-1001`. The synthetic failure ID is not a persisted
destination.
