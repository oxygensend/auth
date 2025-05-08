# Concept

The change password feature allows users to change their password using their current password. It is useful for
maintaining account security and ensuring that users can update their credentials when needed.

After successful password change, domain event is published to the broker. More on that
in [Domain Events](../domain-events.md).

# Set up

Included in REST profile.
For domain events enable profile for message broker (eg. RabbitMQ, Kafka)

# Example for REST API

Input: POST request

```json
{
  "oldPassword": "oldPassword",
  "newPassword": "newPassword"
}
```

Output: 204 No Content
