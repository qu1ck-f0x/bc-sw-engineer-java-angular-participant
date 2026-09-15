# Lab 16 — Catch Order

## Step 1 — List types

BusinessException (notFound/conflict), Validation failures, Exception.

## Step 2 — Order

BusinessException → validation → Exception (top to bottom).

## Step 3 — Why

Broad catch first would shadow domain mapping.

## Scope

Pre-lab only.