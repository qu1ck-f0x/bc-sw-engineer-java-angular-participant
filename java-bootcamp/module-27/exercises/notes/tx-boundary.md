# Lab 27 - Transaction boundary

Put `@Transactional` on the public `TransferService.transfer` method. The controller calls the injected service bean
through Spring's transaction proxy, so debit, credit, and the log share one unit of work. Do not put separate
transactions in the controller or each repository action: a committed debit followed by a failed credit would be wrong.
A call from one method of the same service to another annotated method can bypass the proxy (self-invocation), so that
is not a reliable way to begin this transaction.
