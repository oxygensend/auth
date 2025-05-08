# Concept

The Auth Service is responsible solely for managing authentication data. It does not store or handle user-specific
details such as first name, last name, or address. These attributes should be managed by dedicated services (e.g., a
User Profile Service).

### Business Identifier (businessId)

To link a user across services, you can use the businessId, a unique identifier representing the user in your system.
Although optional, it is recommended for improved service integration. The businessId is also included in the access
token payload, making it easily accessible across services.

### Registration and Rollback

User registration often spans multiple services. A failure in any part of this flow can lead to a partially registered
user and inconsistent state. To handle this, a rollback mechanism is necessary.

The Auth Service emits a domain event after a successful registration. This makes it possible to implement the Saga
Pattern, which coordinates compensating actions (such as deleting partial data) if any step in the registration fails.

Alternatively, if you choose not to use an event-driven approach, you can implement a manual rollback mechanism using an
administrator user. This user, with elevated permissions, can delete incomplete registrations if a failure occurs.

### Account Activation

The Auth Service supports configurable account activation strategies. The chosen strategy is included in the domain
event emitted after successful registration, allowing other services to adapt their behavior accordingly.

Available activation types:

    none: Account is activated immediately.

    email-verify: Activation occurs after email verification.

    password-change: Activation occurs after the user changes their password.

### Example: Email Verification Registration Flow

1. User initiates registration.

2. A downstream service (e.g., Service X) registers the user in the Auth Service.

3. Upon success, the Auth Service emits a domain event containing user information.

4. Service X continues its registration logic synchronously.

5. It receives the domain event and requests an email verification token from the Auth Service.

6. The Auth Service generates a unique token.

7. Service X constructs a verification URL and sends it to the user via email.

8. The user clicks the link and is redirected to Service X’s UI.

9. Service X sends the token to the Auth Service for verification.

10. The Auth Service validates the token and activates the user account.

# Set up

Included in REST profile.
For domain events enable profile for message broker (eg. RabbitMQ, Kafka)

Auth Service allows you to specify the registration settings in the configuration file. You can choose whether to
activate the account after registration and if so, which method to use.

```yaml
  settings:
    sign-in:
      account-activation: none # Should the account be activated after registration, possible values: none, email-verify, password-change, default: none
```

# Example for REST API

## Registration

Input: POST request

```json
{
  "username": "test1234",
  "password": "test1234",
  "email": "test@test.pl",
  "roles": [
    "ROLE_USER"
  ],
  "businessId": "string"
}
```

Output: 201 Created with userId

```json
{
  "userId": "string",
  "accessToken": "token",
  "refreshToken": "token"
}
```