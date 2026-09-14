# Resource and URI Sketch

Module 13 Ex 1 - completed from the starter sketch.

## Step 1 — Resource

Customer is the resource; agents act on customers, not SOAP operations.

## Step 2 — URIs

Collection `/api/v1/customers`; item `/api/v1/customers/{customerId}`.

## Step 3 — Fixtures

Plan `CUS-1001` Amina ACTIVE and `CUS-1002` Ravi PROSPECT as item examples.

## Step 4 — Capture

                 CUSTOMER RESOURCE
                        │
                        ▼
             /api/v1/customers
              Customer Collection
                        │
          ┌─────────────┴─────────────┐
          │                           │
          ▼                           ▼

/api/v1/customers/CUS-1001 /api/v1/customers/CUS-1002
│ │
▼ ▼
Amina — ACTIVE Ravi — PROSPECT

collection /api/customers
item /api/customers/CUS-1001
sub-resource /api/customers/CUS-1001/interactions

Design only - no Boot hosting, controller, or OpenAPI YAML yet.

