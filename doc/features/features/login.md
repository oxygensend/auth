# Concept

The login feature allows users to authenticate themselves using their credentials (username or email and password) and
obtain an
access token and refresh token. Access token can be used to access protected resources and perform actions on behalf of
the user.
Refresh token can be used to obtain a new access token when the current one expires.

# Set up

Included in REST profile.

Auth service allows you to specify the login type in the configuration file. The login type can be either email or
username. It describes which property will be used to log in the user.

```yaml
auth:
  settings:
    login-type: email # login type for authentication, possible values: email, username, default: email


```

# Example for REST API

## Login

Input: POST request

```json
{
  "login": "test1234",
  "password": "test1234"
}
```

Output: 200 OK with access token and refresh token

```json
{
  "accessToken": "token",
  "refreshToken": "token"
}
```

## Refresh token

Input: POST request

```json
{
  "token": "token"
}
```

Output: 200 OK with new access token and refresh token

```json
{
  "accessToken": "token",
  "refreshToken": "token"
}
```
