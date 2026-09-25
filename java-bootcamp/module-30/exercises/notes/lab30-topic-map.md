# Lab 30 - Topic and key map

| Concept            | Northstar choice                       |
|--------------------|----------------------------------------|
| Main topic         | `crm.customer-events.v1`               |
| Dead-letter topic  | `crm.customer-events.v1.dlq`           |
| Lab partitions     | 3 on main, 1 on DLQ                    |
| Replication factor | 1 for the single-node lab only         |
| Record key         | `customerId`: `CUS-1001` or `CUS-1002` |

Kafka hashes the same non-null key to the same partition while the partition count and partitioner stay stable. Amina's
created and status-changed events can therefore be ordered by offset within one partition. Ravi may land elsewhere;
there is no global order across partitions. A null or random key loses this per-customer affinity. One versioned topic
plus `eventType` lets related customer facts share the key/order contract; separate topics can help differing retention
or access policies but make cross-type order harder.

The `.v1` name freezes a consumer contract so a breaking v2 can be migrated deliberately. A poison record that
repeatedly fails deserialization or violates a supported schema is a conceptual DLQ candidate; Lab 31 will wire that
behavior. The existence of the DLQ topic alone does not route failures.
