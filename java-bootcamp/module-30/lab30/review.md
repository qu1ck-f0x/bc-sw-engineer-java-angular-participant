# Module 30 lab review

## 1. Read the prompts and keep work in the module

I read the lab guide, all six exercise prompts, starter code, Compose file, JSON samples, and saved
`private/workflow.md`. I copied `lab30/starter` to `lab30/completed` and copied the exercise templates to
`exercises/notes`, removing `.TODO` from the finished filenames. I kept all edits inside `java-bootcamp/module-30` and
left the course README and guide unchanged. I followed the guide's exercise order: 1, 2, 4, 3, 5, 6.

## 2. Work through the pre-lab exercises

I explained why synchronous calls to notification, audit, and analytics can make customer creation slow and fragile,
while an already-published event can be processed after the Customer JVM is down. I froze `crm.customer-events.v1` with
three partitions, `crm.customer-events.v1.dlq` with one, and `customerId` as the key. I distinguished topic, partition,
per-partition offset, broker, and consumer group. I sketched versioned envelopes, including `eventId`, UTC `occurredAt`,
and `lab-request-001`, and wrote the producer settings checklist. I marked the note content complete but the broker
readiness gate **Pending** because Docker and a shared bootstrap are unavailable.

## 3. Inspect and complete the three JSON samples

The starter's Amina-created event was already a complete version-1 envelope. Ravi-created and Amina-status-changed still
had `TODO-replace-with-uuid` IDs, so I replaced those with distinct UUIDs and kept their existing fictional fixtures.
The two Amina files have root `customerId=CUS-1001`; Ravi has `CUS-1002`. Each has `correlationId=lab-request-001`,
`source=customer-service`, and an ISO-8601 UTC timestamp. I did not invent a separate Ravi status-change file just
because the pre-lab sketch asks for that conceptual example.

## 4. Finish the Java producer

The starter configured serializers but had no acknowledgment/idempotence settings and threw
`UnsupportedOperationException` instead of sending. I set the required properties and client ID:

```java
props.put(ProducerConfig.ACKS_CONFIG, "all");
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
props.put(ProducerConfig.CLIENT_ID_CONFIG, "lab30-customer-producer");
```

I parse the JSON before opening a producer, checking the required envelope fields, UUID, timestamp, supported event
type, version 1, and customer ID format. The key comes from that parsed root `customerId`, not a hardcoded `CUS-1001`,
so the producer also works for Ravi's file. The send uses the exact main topic and waits for metadata:

```java
ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.customerId(), event.json());
var metadata = producer.send(record).get(30, TimeUnit.SECONDS);
System.out.printf("topic=%s key=%s partition=%d offset=%d timestamp=%d%n",
    metadata.topic(), event.customerId(), metadata.partition(), metadata.offset(), metadata.timestamp());
```

The output omits the JSON body to avoid dumping even fictional contact data into logs. I kept environment overrides for
`KAFKA_BOOTSTRAP_SERVERS` and `CRM_EVENTS_TOPIC` and added an optional JSON-file argument. The default file is
Amina-created.

## 5. Add a broker-free validation path and focused tests

I added `--validate [event-file]` so a learner can run the real command-line entry point when Kafka is unavailable. It
checks the same envelope and prints `validated ... (not published)` before any producer is opened. This is explicitly
not a delivery test. I added JUnit and Surefire to the POM and wrote five tests for producer properties, all three
sample envelopes and distinct IDs, same-customer key consistency, invalid-envelope rejection, and the validation-only
CLI. A test constructs a `ProducerRecord` and checks that its key equals the envelope customer ID; it does not pretend
to have a broker-assigned partition.

## 6. Check the runtime honestly

The Windows shell has no `docker` command, and Ubuntu WSL reported no Docker integration. The user confirmed there is no
shared Kafka bootstrap. I therefore could not start KRaft, create or describe the two topics, publish with console or
Java, consume key/partition/offset metadata, compare `crm-notifications` with `crm-audit`, or measure lag. I left those
checkpoint rows **Pending**, not Pass. The supplied `compose.yaml` remains in `completed` for a future broker run.

My first Maven attempt stopped at a Windows compiler-resource access error before tests ran. Re-running with the
required filesystem permission passed **5 tests, 0 failures, 0 errors**. `mvn -B package` passed, and the actual Maven
exec command in `--validate` mode printed the expected `CUS-1001` key with `(not published)`.

## 7. Write the hand-off runbook

I replaced the starter TODO notes with `completed/docs/kafka-notes.md`. It records the frozen topic/key contract,
per-key ordering and at-least-once implications, exact offline and future Docker commands, compact keyed console input,
metadata consumption, Java producer invocation, group/lag procedure, DLQ purpose, failure experiments, and production
TLS/SASL/replication limits. I was careful to label commands that have **not** been executed against a broker. This lets
a later run collect real evidence without confusing a proposed output with an observation.

## 8. Clean up and remaining work

I scanned completed code and notes for TODOs and placeholder UUIDs, kept useful starter prompts as finished comments,
removed the copied starter folders and generated Maven `target` output after verification, and kept finished work within
`module-30`. The only outstanding lab work is broker-dependent proof: run Compose or provide a shared broker,
create/describe the topics, publish and consume keyed events, observe same-key partition/offset order, compare the two
consumer groups, and record lag. Until then, the full Lab 30 broker checkpoint is not complete.
