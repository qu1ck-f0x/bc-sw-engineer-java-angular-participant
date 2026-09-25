# Lab 30 readiness

| Earlier exercise file         | Present and answered? |
|-------------------------------|-----------------------|
| `lab30-prelab-eda.md`         | yes                   |
| `lab30-topic-map.md`          | yes                   |
| `lab30-kafka-todos.md`        | yes                   |
| `lab30-envelope-sketch.md`    | yes                   |
| `lab30-producer-checklist.md` | yes                   |

| Runtime gate                                         | Result |
|------------------------------------------------------|--------|
| Docker CLI / Compose available                       | no     |
| Shared Kafka bootstrap supplied                      | no     |
| Main and DLQ topic names frozen                      | yes    |
| Amina `CUS-1001` ACTIVE and Ravi `CUS-1002` PROSPECT | yes    |
| Spring Kafka avoided (raw client lab)                | yes    |

**Self-mark:** The pre-lab note content is Pass, but the broker hard gate is **Pending**. I can compile, test, and
validate event files offline; I cannot honestly mark broker startup, topic creation, keyed produce/consume, or group lag
Pass until a Kafka broker is provided.
