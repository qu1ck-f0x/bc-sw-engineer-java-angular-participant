# Lab 30 - Why async for CRM

## Synchronous fan-out pain

If creating Amina (`CUS-1001`, ACTIVE) waits for email, audit, and analytics HTTP calls in the same request thread,
three things go wrong: latency is the sum of dependent calls; a slow or down notification service can fail or delay
customer creation; and the Customer service must know every downstream endpoint and retry rule. A new consumer requires
another synchronous integration.

## Event idea

After the customer change is committed, publish a `CustomerCreated` fact with `customerId=CUS-1001` and
`correlationId=lab-request-001`; notification, audit, and analytics can process it in separate consumer groups.
Publishing must still be reliable: this lab does not solve database-to-Kafka atomicity, so a production design would
consider an outbox.

## Coupling check

**False:** the Customer JVM need not be up for Audit to process an event already retained by Kafka. Kafka must be
available and the audit consumer must have access to the record. HTTP remains useful for queries and immediate
responses; events are for independent side effects.
