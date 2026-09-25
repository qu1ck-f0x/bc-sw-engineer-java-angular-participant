# Lab 29 - Exception to status map

| Case                 | HTTP | Existing `error` / `message`             |
|----------------------|-----:|------------------------------------------|
| Invalid DTO          |  400 | Bad Request / Validation failed          |
| Malformed JSON       |  400 | Bad Request / Malformed request body     |
| Missing `CUS-9999`   |  404 | Not Found / Customer not found           |
| Duplicate `CUS-1001` |  409 | Conflict / Duplicate customer            |
| Unexpected exception |  500 | Internal Server Error / Unexpected error |

The exercise asks for a `code`, but `ErrorResponse` has no code property, so these are the fields the client really
receives. The timed starter has no illegal-transition endpoint; a later status-update feature would need its own rule.
An expected missing customer must not be 500, and the duplicate is 409 rather than 400.
