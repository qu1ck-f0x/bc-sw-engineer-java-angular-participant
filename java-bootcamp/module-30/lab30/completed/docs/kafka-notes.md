# Lab 30 - Kafka runbook and evidence

## Frozen contract

| Item              | Lab value                                                             |
|-------------------|-----------------------------------------------------------------------|
| Host bootstrap    | `localhost:9092`                                                      |
| Broker            | Single-node Apache Kafka 3.9.1 KRaft in `compose.yaml`                |
| Main topic        | `crm.customer-events.v1`, 3 partitions, replication factor 1          |
| Dead-letter topic | `crm.customer-events.v1.dlq`, 1 partition, replication factor 1       |
| Record key        | Root `customerId`, such as `CUS-1001` or `CUS-1002`                   |
| Correlation       | `lab-request-001` in the JSON envelope                                |
| Groups            | `crm-notifications` (competing members) and `crm-audit` (independent) |

The producer sends a `CustomerCreated` or `CustomerStatusChanged` JSON envelope to the main topic. Kafka appends it to
one partition and returns metadata containing the partition and offset. Consumers in each group read and commit their
own positions. Two members of the same group split partitions, so they do **not** each receive every record; the
separate audit group receives its own copy of the stream.

Using the same non-null `customerId` key gives per-customer partition affinity and order by offset. It does not give a
global order between Amina and Ravi, and changing the partition count can change future key placement. Consumers can see
duplicates after replay/rebalance, so Lab 31 should deduplicate by `eventId`. The DLQ topic is reserved for poison or
repeatedly failing records; this lab creates the topic but does not wire failure routing.

## What was verified here

The five offline JUnit tests passed with no failures or errors. They checked `acks=all`, idempotence, client ID, all
three version-1 event samples and distinct UUIDs, customer-derived keys, invalid-envelope rejection, and validation-only
CLI execution. `mvn -B package` passed. I also ran the Maven exec command below and observed
`validated topic=crm.customer-events.v1 key=CUS-1001 ... (not published)`.

**Broker gate: unverified.** `docker` was not available in Windows or Ubuntu WSL, and no shared bootstrap server was
supplied. Therefore I did not start KRaft, create or describe topics, publish, consume, inspect partition/offset
metadata, compare groups, or measure lag. The commands below are a reproducible hand-off, **not recorded evidence of a
live Kafka run**.

## Offline commands

Run from `module-30/lab30/completed`:

```powershell
mvn -B test
mvn -B package
mvn -B -q exec:java '-Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer' '-Dexec.args=--validate events/customer-created-amina.json'
mvn -B -q exec:java '-Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer' '-Dexec.args=--validate events/customer-created-ravi.json'
mvn -B -q exec:java '-Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer' '-Dexec.args=--validate events/customer-status-changed-amina.json'
```

`--validate` checks JSON and prints the derived key without opening a producer or publishing anything.

## Broker setup when Docker is available

Run from the same directory. Keep `crm-kafka` as the container name used by the commands. Wait for the broker to be
ready before creating topics; repeat the list command if startup is still in progress.

```powershell
docker compose up -d
docker compose ps
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic crm.customer-events.v1 --partitions 3 --replication-factor 1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic crm.customer-events.v1.dlq --partitions 1 --replication-factor 1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic crm.customer-events.v1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic crm.customer-events.v1.dlq
```

The describe output must show 3 partitions on the main topic and 1 on the DLQ. These are not claimed as observed yet. If
host port 9092 is occupied, change the published host port and `KAFKA_BOOTSTRAP_SERVERS` together. A host Java process
uses `localhost:9092`; a container-to-container client would need a suitable container listener/advertised address
instead.

## Produce and consume when a broker is available

For CLI publishing, run this interactive producer and paste the three compact keyed lines below. Exit with Ctrl+C. Do
not paste pretty-printed multi-line JSON directly, because the console producer treats each input line as a separate
record.

```powershell
docker exec -it crm-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --property parse.key=true --property key.separator=:
```

```text
CUS-1001:{"eventId":"e4b5f53d-7f18-4cf4-81ce-7cab6ec98491","eventType":"CustomerCreated","eventVersion":1,"occurredAt":"2026-07-13T06:00:00Z","customerId":"CUS-1001","correlationId":"lab-request-001","source":"customer-service","data":{"fullName":"Amina Khan","status":"ACTIVE"}}
CUS-1002:{"eventId":"a1c2e3f4-1111-4222-8333-444455556666","eventType":"CustomerCreated","eventVersion":1,"occurredAt":"2026-07-13T06:05:00Z","customerId":"CUS-1002","correlationId":"lab-request-001","source":"customer-service","data":{"fullName":"Ravi Singh","status":"PROSPECT"}}
CUS-1001:{"eventId":"7c71eb18-0a60-43de-98c3-655f74aa3bac","eventType":"CustomerStatusChanged","eventVersion":1,"occurredAt":"2026-07-13T06:10:00Z","customerId":"CUS-1001","correlationId":"lab-request-001","source":"customer-service","data":{"previousStatus":"PROSPECT","status":"ACTIVE"}}
```

Then consume with metadata:

```powershell
docker exec crm-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --from-beginning --property print.key=true --property print.partition=true --property print.offset=true --property print.timestamp=true --max-messages 3
```

The two `CUS-1001` records should show the same partition with increasing offsets. `CUS-1002` may share or differ in
partition; its offset is not comparable globally with Amina's. For Java publishing, after the broker is ready, run:

```powershell
mvn -B -q exec:java '-Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer' '-Dexec.args=events/customer-created-amina.json'
```

The expected output shape is `topic=... key=CUS-1001 partition=... offset=... timestamp=...`. This command was **not**
run against a broker here. A broker failure or timeout is not a successful send. Re-running the command can publish the
same event again; producer idempotence only deduplicates retries within one producer session.

## Consumer groups and lag when a broker is available

Start two console consumers in separate terminals with `--group crm-notifications` and a third with `--group crm-audit`.
Use `--from-beginning` for a new group that must read earlier records, then publish new records while they are
listening. The two notifications members should split partition assignments; audit should independently receive the
stream.

```powershell
docker exec -it crm-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --group crm-notifications --from-beginning --property print.key=true --property print.partition=true
docker exec -it crm-kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic crm.customer-events.v1 --group crm-audit --from-beginning --property print.key=true --property print.partition=true
docker exec crm-kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group crm-notifications
docker exec crm-kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group crm-audit
```

Let the notifications group consume and commit, stop **all** its members, publish additional records, and describe the
group again. Its `LOG-END-OFFSET` should exceed `CURRENT-OFFSET`, giving non-zero `LAG`; restarting a member should move
lag toward zero. A group with no committed offset may show no usable lag yet. Save actual describe output before
claiming this checkpoint.

## Failure experiments and production checklist

When a broker is available, intentionally stop it and observe send failure; publish with a null/wrong key and inspect
changed partition affinity; replay from the beginning and see duplicate delivery; and stop the consumer group to observe
lag. None of those experiments were executed in this environment. Do not treat a misspelled topic as success: create
topics explicitly and consider disabling auto-create outside this starter.

PLAINTEXT and replication factor 1 are lab-only. Production needs TLS/SASL, authorization for producer/consumer
principals, replicated brokers/topics, monitoring and alerting on lag, schema compatibility policy, retention/replay
policy, and idempotent consumers. Do not put secrets or unnecessary personal data in event payloads. Avoid
`docker compose down -v` unless intentionally deleting lab data; `docker compose down` stops the local lab after
capturing evidence.

## Checkpoint status

| Checkpoint                                              | Status         |
|---------------------------------------------------------|----------------|
| Exercise notes and frozen contract                      | Pass           |
| Versioned JSON samples and offline validation           | Pass           |
| Java producer config and five unit tests                | Pass           |
| KRaft Up, topics created/described                      | Pending broker |
| CLI/Java live produce and consume metadata              | Pending broker |
| Competing groups and lag                                | Pending broker |
| Production limits and no committed secrets/build output | Pass           |
