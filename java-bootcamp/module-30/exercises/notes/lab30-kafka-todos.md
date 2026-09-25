# Lab 30 - Kafka basics in CRM words

1. A **topic** is a named stream of records, such as `crm.customer-events.v1`.
2. A **partition** is an ordered subset of the topic. The primary lab topic has three.
3. An **offset** is a record's position within one partition, not a global message ID. A consumer group tracks its own
   committed position per partition.
4. A **consumer group** shares partitions among its members. Two members of `crm-notifications` compete; the separate
   `crm-audit` group independently receives the stream.

A **broker** stores and serves the topic partitions. In this lab a single KRaft broker hosts the partitions with
replication factor 1, which has no replica failover. Consumers joining after records were produced need an appropriate
offset policy or a new group with `--from-beginning` to see old records.
