# Concept

The Auth Service is designed for use within a microservices architecture. In this context, it typically manages only
authentication data and relies on other services—such as a User Profile Service—to handle user-specific details.

During the user registration process, multiple services may be involved. If one of these services fails to complete its
part of the registration, it can lead to an inconsistent state where the user is only partially registered. To prevent
this, a rollback mechanism is needed to undo the registration across all involved services.

To coordinate this, the Auth Service emits a domain event after a successful registration. Because of this event-driven
communication, implementing a Saga Pattern is necessary. The saga ensures that if one service fails, compensating
actions (such as deleting partially registered data) are triggered in the other services to maintain consistency.

# Set up

Included in Kafka or RabbitMQ profile.
Set up the broker and topic according to the configuration documentation of the broker you are
using [see](../../configuration/config.md).

# Event structure

The event is published to the broker with the following structure:

```json
{
  "userId": "string",
  "reason": "string",
  "occurredOn": "long"
}
```