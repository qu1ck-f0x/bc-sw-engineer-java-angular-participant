# Module 27 lab review

## 1. Inspect and restore the module layout

I checked the saved workflow and the current workspace. The completed directories from earlier work were missing, while
the starter files were present. I copied the starter into `lab27/completed` and the note templates into
`exercises/notes`, leaving the course guide and README alone. I used the seeded IDs `ACC-MAIN-1001` and
`ACC-LOYALTY-1001`; some exercise wording uses a different ID order, so following the actual seed avoided misleading
tests.

## 2. Complete the six exercises

I filled the ACID table, transaction-boundary note, rollback plan, transfer pseudocode, propagation warnings, and
readiness checklist. The important insight was that an exception thrown before any debit would not demonstrate rollback.
My plan explicitly flushes the source update before throwing for `ACC-FORCE-FAIL` and then reads both balances and the
log afterward.

## 3. Implement the transfer transaction

I added `@Transactional` to the public service method. I reject null/nonpositive amounts, identical or missing IDs,
missing source accounts, and insufficient funds. I subtract from the source and flush it before the synthetic failure;
then I load and credit the destination and save one log row in the same transaction. These modified lines carry the
rollback demonstration:

```java
from.setBalance(from.getBalance().subtract(amount));
accountRepository.saveAndFlush(from);
if ("ACC-FORCE-FAIL".equals(toAccountId)) {
  throw new IllegalStateException("Forced transfer failure for rollback demo");
}
```

The success path then adds to the destination and saves the log:

```java
to.setBalance(to.getBalance().add(amount));
accountRepository.save(to);
TransactionLog log = new TransactionLog();
log.setFromAccountId(fromAccountId);
log.setToAccountId(toAccountId);
log.setAmount(amount);
transactionLogRepository.save(log);
```

I rewrote useful TODO prompts as `Finished prompt` comments. I did not split debit, credit, or logging into separate
transactions or catch the forced exception.

## 4. Replace the weak starter test

The original test only checked MAIN after a broad `Exception` assertion. I added `@BeforeEach` to reset both seeded
balances and clear logs, plus an `assertLedger` helper that checks both accounts and log count. The forced failure
checks the exact exception and the unchanged ledger. The happy path checks MAIN 995.00, LOYALTY 55.00, and the log's
source, destination, and 5.00 amount. I added missing-destination rollback, insufficient-funds, and nonpositive-amount
cases. The crucial assertion is:

```java
assertLedger("1000.00", "50.00", 0);
```

That assertion runs after the flushed debit throws, so it proves more than merely reaching an error line.

## 5. Verify and document the evidence

`mvn -B test` passed **5 tests with no failures or errors**. Packaging passed. I ran the jar on a temporary local port:
a valid POST returned **200** and `{"status":"OK"}`, while the forced failure returned **500**. I stopped the server
afterward. The live API has no account-read endpoint, so the balance and log evidence comes from the integration tests.
I wrote `completed/docs/acid-notes.md` with the measured ledger results and the H2 in-memory limit, and
`completed/docs/transaction-review.md` with the transaction decisions and remaining concurrency/idempotency concerns.

## 6. Clean up

I scanned the finished source and notes for placeholders, removed the copied starter directories and generated Maven
output after verification, and kept the final code and notes within `module-27`. The original course prompt files remain
as supplied.
