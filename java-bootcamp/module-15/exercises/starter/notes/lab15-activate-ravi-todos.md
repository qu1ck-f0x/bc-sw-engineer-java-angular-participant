# Lab 15 — Fill Activate Ravi Pseudocode TODOs

customer = repo.findById (CUS-1002)
if null → NotFound
if status is not PROSPECT → domain/illegal transition
set status ACTIVE
repo.save/update (customer)
log correlation lab-request-001

Repository saves state; it does not decide PROSPECT→ACTIVE.

## Scope

Pre-lab only.

findById (_____) · require _____ · set _____ · save · correlation _____
