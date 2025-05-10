# Concept

The reset password feature allows users to reset their password using a token sent to their email.
It is useful for recovering access to an account when the user forgets their password or needs to change it for security
reasons.

<b>Auth service provides only tools for generating an unique token and reset password functionality. This allows you to
implement your own password reset flow.</b>

- generate password reset token based on userId
- reset password using token

After successful password reset, domain event is published to the broker.  More on that
in [Domain Events](../domain-events.md).

Example password reset flow:

1. User requests a password reset token using his identifier (userId).
2. Auth service generates a unique token.
3. 3th part service receives the token and generates appropriate url for his UI and sends it to the user via email.
4. User clicks the link in the email and is redirected to the 3th part service UI.
5. User enters a new password and submits the form.
6. 3th part service sends a request to the auth service with the token and new password.
7. Auth service verifies the token and updates the user's password.
8. User receives a confirmation message that their password has been reset successfully.

# Set up
Included in REST profile.
For domain events enable profile for mesage broker (eg. RabbitMQ, Kafka)