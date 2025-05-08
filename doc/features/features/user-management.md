# Concept

The user management feature allows administrators to manage user accounts, including creating with registration bypass,
blocking, unblocking, and deleting users.

- create user
- block user
- unblock user
- delete user

After successful operations, domain event are published to the broker. More on that
in [Domain Events](../domain-events.md).

# Set up

Included in REST profile.
For domain events enable profile for message broker (eg. RabbitMQ, Kafka)
