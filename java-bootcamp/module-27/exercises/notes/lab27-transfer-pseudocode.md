# Lab 27 - Transfer pseudocode

```text
public @Transactional transfer(fromId, toId, amount):
  reject null, zero, or negative amount and same-account IDs
  load source; reject insufficient funds
  subtract amount from source and saveAndFlush it
  if destination is ACC-FORCE-FAIL: throw IllegalStateException
  load destination; add amount and save it
  save one TransactionLog(fromId, toId, amount)
```

The forced exception comes **after** a flushed debit so rollback is observable. It must escape the public service
method. `saveAndFlush` does not commit; credit and log still belong to the same transaction.
