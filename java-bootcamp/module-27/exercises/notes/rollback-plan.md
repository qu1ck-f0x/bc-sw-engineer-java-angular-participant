# Lab 27 - Rollback proof plan

1. Reset MAIN to 1000.00, LOYALTY to 50.00, and clear the log.
2. Call `transfer("ACC-MAIN-1001", "ACC-FORCE-FAIL", 10.00)`. Debit MAIN and call `saveAndFlush` before throwing the
   requested `IllegalStateException`.
3. In a separate repository read after the exception, verify MAIN 1000.00, LOYALTY 50.00, and log count 0. A flush
   issued SQL but was not a commit.
4. Run a normal 5.00 transfer and verify MAIN 995.00, LOYALTY 55.00, and one log with the correct fields. The combined
   balance must stay 1050.00.
