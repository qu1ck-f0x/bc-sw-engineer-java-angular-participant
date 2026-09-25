# Lab 27 - ACID evidence

The test fixture resets `ACC-MAIN-1001` to 1000.00, `ACC-LOYALTY-1001` to 50.00, and the log to empty before each case.

| Test                               | MAIN after | LOYALTY after | Logs after |
|------------------------------------|-----------:|--------------:|-----------:|
| Force failure on 10.00             |    1000.00 |         50.00 |          0 |
| Successful 5.00 transfer           |     995.00 |         55.00 |          1 |
| Missing destination after debit    |    1000.00 |         50.00 |          0 |
| Insufficient or nonpositive amount |    1000.00 |         50.00 |          0 |

**Atomicity:** The debit is sent to H2 with `saveAndFlush` before the forced exception, but the failed transaction
leaves no partial account change or log. **Consistency:** The successful transfer preserves the combined 1050.00 balance
and creates one matching log. **Isolation:** We did not run concurrent requests or measure the configured isolation
level, so we cannot claim that lost updates are prevented. **Durability:** The successful commit can be read while H2 is
running, but `jdbc:h2:mem:lab27` loses data on restart. This lab does not prove persistent durability.

Live HTTP on a temporary local port returned 200 with `{"status":"OK"}` for a valid transfer and 500 for
`ACC-FORCE-FAIL`. The post-transfer balances and log counts above come from repository-backed tests, not from an
account-read HTTP endpoint.
