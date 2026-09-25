# Lab 27 - Propagation warnings

- `REQUIRES_NEW` on debit, credit, or log could commit one part independently. Keep the repository calls in the service
  method's default REQUIRED transaction.
- A direct call between methods on the same bean bypasses Spring's transaction proxy. Enter through the injected service
  bean.
- Catching and swallowing the forced unchecked exception can allow an unintended commit. Let it escape (or explicitly
  mark rollback).
- A flush sends SQL to H2 but is not a commit. The rollback test proves the flushed debit was undone.
