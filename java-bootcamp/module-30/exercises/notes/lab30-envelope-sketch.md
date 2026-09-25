# Lab 30 - Event envelope sketch

Every event carries `eventId`, `eventType`, `eventVersion`, `occurredAt` in UTC, root `customerId`, `correlationId`,
`source`, and a `data` object. The Kafka record key is the same root `customerId`.

```json
{
  "eventId": "e4b5f53d-7f18-4cf4-81ce-7cab6ec98491",
  "eventType": "CustomerCreated",
  "eventVersion": 1,
  "occurredAt": "2026-07-13T06:00:00Z",
  "customerId": "CUS-1001",
  "correlationId": "lab-request-001",
  "source": "customer-service",
  "data": { "fullName": "Amina Khan", "status": "ACTIVE" }
}
```

A conceptual Ravi status update would use a new `eventId`, `eventType=CustomerStatusChanged`, `customerId=CUS-1002`, and
`data` such as `{"previousStatus":"PROSPECT","status":"ACTIVE"}`. The lab's actual Ravi sample is `CustomerCreated`, and
its actual status-change sample is for Amina; this sketch does not claim a Ravi update was published. Consumers should
ignore unknown additive `data` fields but reject incompatible versions until they support them. Keep credentials,
account numbers, and unnecessary PII out of events.
