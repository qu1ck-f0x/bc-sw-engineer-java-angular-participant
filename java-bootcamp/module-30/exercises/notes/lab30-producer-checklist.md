# Lab 30 - Producer checklist

- [x] Bootstrap defaults to host `localhost:9092`; an alternate broker can be supplied with `KAFKA_BOOTSTRAP_SERVERS`.
- [x] `acks=all` waits for acknowledgments from the in-sync replicas before a send is reported successful. With this
  lab's replication factor 1, it still means only one broker acknowledged.
- [x] `enable.idempotence=true` deduplicates producer retries within the producer session. It does not prevent
  duplicates from rerunning the application or replaying an event; consumers should use `eventId` for idempotency.
- [x] Record key comes from the envelope's `customerId`, not a random value. The value is the versioned JSON envelope.
- [x] The Java producer waits for send metadata and prints topic, key, partition, offset, and timestamp without logging
  the full payload.
- [x] A send timeout or unavailable broker is a failure, not proof of delivery. Check broker health and advertised
  listeners before retrying.

The pre-lab exercise itself did not run `kafka-console-producer`. Broker commands belong to the lab runbook, and live
proof remains pending because no Docker or shared Kafka broker is available in this environment.
