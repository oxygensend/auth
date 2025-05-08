# Domain Events

List of domain events published by the service. Domain events are used to notify other services of important events


- AddedRoleEvent 
  - Published when a new role is assigned to a user.
  - ```json
    {
        "userId": "uuid",
        "role": "string",
        "occurredOn": "long",
    }
    ```
  - Example:
    ```json
    {
        "userId": "123e4567-e89b-12d3-a456-426614174000",
        "role": "ADMIN",
        "occurredOn": 1691234567890
    }
    ```
- RemovedRoleEvent
  - Published when a role is removed from a user.
  - ```json
    {
        "userId": "uuid",
        "role": "string",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "role": "ADMIN",
            "occurredOn": 1691234567890
        }
        ```
- BlockedEvent
  - Published when a user is blocked.
  - ```json
    {
        "userId": "uuid",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "occurredOn": 1691234567890
        }
        ```
- UnblockedEvent
  - Published when a user is unblocked.
    - ```json
      {
          "userId": "uuid",
          "occurredOn": "long",
      }
      ```
     - Example:
          ```json
          {
              "userId": "123e4567-e89b-12d3-a456-426614174000",
              "occurredOn": 1691234567890
          }
          ```
- VerifiedEvent
  - Published when a user is verified, eg. through email verification or password reset.
  - ```json
    {
        "userId": "uuid",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "occurredOn": 1691234567890
        }
        ```
- PasswordChangedEvent
  - Published when a user changes their password.
  - ```json
    {
        "userId": "uuid",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "occurredOn": 1691234567890
        }
        ```
- PasswordResetEvent
  - Published when a user resets their password.
  - ```json
    {
        "userId": "uuid",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "occurredOn": 1691234567890
        }
        ```
- RegisteredEvent 
  - Published when a new user registers.
  - ```json
    {
        "userId": "uuid",
        "businessId": "string",
        "email": "string",
        "accountActivationType": "string",
        "occurredOn": "long",
    }
    ```
   - Example:
        ```json
        {
            "userId": "123e4567-e89b-12d3-a456-426614174000",
            "businessId": "business-123",
            "email": "test@test;com",
            "accountActivationType": "email-verify",
            "occurredOn": 1691234567890
        }
        ```

   


Metadata:
- aggregateType - 'string'
- eventType - 'string'
- id - long

are always included in the event message, could be in the header or in the body.
eg. for Kafka data is included in the header;

```json
{
  "spring_json_header_types": "{\"eventType\":\"java.lang.String\",\"aggregateType\":\"java.lang.String\"}",
  "eventType": "com.oxygensend.auth.domain.model.identity.event.RegisteredEvent",
  "aggregateType": "com.oxygensend.auth.domain.model.identity.User"
}
```
