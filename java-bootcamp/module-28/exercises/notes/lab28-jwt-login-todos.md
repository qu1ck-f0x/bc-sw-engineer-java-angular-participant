# Lab 28 - Login and Bearer flow

`POST /api/auth/login` takes `username` and `password`. `CrmUserDetailsService` supplies `agent1`/`AGENT` and `admin1`/
`ADMIN`; `PasswordEncoder.matches` checks the BCrypt password before `JwtService.issueToken` is called. Success returns
`accessToken` and `tokenType: Bearer`; wrong credentials get 401. Later requests send
`Authorization: Bearer <accessToken>`, not a token in the URL query string.

`JWT_SECRET` feeds `northstar.security.jwt-secret`; `.env.example` contains only a lab placeholder. The timed-path
`lab.<subject>.<role>.<sig>` token is not a real signed or expiring JWT. No real secret belongs in these notes or Git.
