# Lab 20 — MDC Lifecycle

Put: MDC.put ("corr", "lab-request-001") on entry.

Use: service logs include corr via %X{corr}.

Clear: finally { MDC.clear (); }

Metrics/Actuator wait for Lab 21.

## Scope

Pre-lab only.