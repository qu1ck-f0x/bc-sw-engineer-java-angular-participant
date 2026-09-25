# Lab 28 - Production identity checklist

- Replace the teaching stub and hardcoded users with an enterprise OAuth2/OIDC identity provider.
- Validate real JWT signature, issuer, audience, and expiration using managed keys/JWKS.
- Store keys in a secret manager; rotate on schedule or incident, with a safe overlap period.
- Use HTTPS, short token lifetimes, least privilege, and reviewed ADMIN grants.
- Audit failures without logging passwords, signing secrets, or raw Bearer tokens.
- If a real key reaches Git, revoke and rotate it; removing a file is not enough.

Keycloak or another authorization server is not required for this lab's timed path.
