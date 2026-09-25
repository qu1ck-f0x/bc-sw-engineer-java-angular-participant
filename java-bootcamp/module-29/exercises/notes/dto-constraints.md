# Lab 29 - DTO constraints

| Actual starter field | Constraint            | Reason                                  |
|----------------------|-----------------------|-----------------------------------------|
| `id`                 | `@NotBlank`           | A customer needs an ID.                 |
| `name`               | `@NotBlank`           | A blank name cannot be stored.          |
| `email`              | `@NotBlank`, `@Email` | Reject missing and malformed addresses. |
| `status`             | `@NotBlank`           | Require a status in this timed lab.     |

The exercise example says `fullName`, but the starter DTO has `name`, so I used the actual field. Controller `@Valid`
triggers these constraints. Duplicate `CUS-1001` is a service rule returning 409, not a field annotation. Additional
`@Size` or allowed-status rules are outside the timed scope.
