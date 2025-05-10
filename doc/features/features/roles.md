## Concept

Auth Service provides a set of APIs to manage user roles. You can assign and remove roles from users, as well as
retrieve the list of roles assigned to a user.
Operations can be performed by admin users only.

After successful assign or removal operations, domain event is published to the broker.

## Set up

Included in REST profile.
For domain events enable profile for message broker (eg. RabbitMQ, Kafka).  More on that
in [Domain Events](../domain-events.md).

Auth Service allows you to specify the role names and their corresponding permissions in the configuration file.

```yaml
auth:
  settings:
    roles: # roles that will be used for authentication for a non-admin user
      - ROLE_USER
    admin-roles: # roles that will be used for authentication for an admin user
      - ROLE_ADMIN
```

