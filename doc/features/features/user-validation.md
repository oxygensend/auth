# Concept

The user validation feature allows you to validate user by access token returning his roles. It is useful for checking
if the user is logged in and has the required permissions to access certain resources or perform specific actions.

# Set up

Included in REST profile.

# Example for REST API

Input: Authorization header with Bearer token

Output: 200 OK with user roles

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "roles": [
    "ROLE_USER",
    "ROLE_ADMIN"
  ]
}
```

