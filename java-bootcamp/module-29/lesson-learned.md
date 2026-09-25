# Module 29 lessons learned

| Part              | Significance                               | What I should learn                                                                                          |
|-------------------|--------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| DTO constraints   | Rejects bad input before business logic.   | Put `@NotBlank`/`@Email` on the request DTO and trigger them with `@Valid`; uniqueness stays a service rule. |
| Handler plan      | Unifies expected failures.                 | Map invalid input to 400, missing customers to 404, duplicates to 409, and hide internal 500 details.        |
| Error envelope    | Gives clients one predictable shape.       | Follow the supplied fields, including correlation and violations, rather than inventing a code field.        |
| Status map        | Improves client behavior and diagnostics.  | A missing record is not a server error; a duplicate is a conflict.                                           |
| MockMvc body plan | Prevents client contract drift.            | Assert status and JSON fields with a Bearer token.                                                           |
| Readiness and lab | Connects validation, advice, and security. | Check success and failure in tests and live HTTP; keep secrets and build output out of submissions.          |
