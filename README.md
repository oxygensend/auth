# Auth Service

## Documentation

- [Profiles](./doc/configuration/profiles.md)
- [Features](./doc/features/features.md)
- [Domain Events](./doc/features/domain-events.md)
- [Configuration](./doc/configuration/config.md)

## Summary

The Auth Service is responsible for managing user authentication and authorization within the system.
It provides a unified interface for handling user credentials, session management, and access control.
Auth Service is a flexible solutions which can be used in various environments, including microservices architectures,
monolithic applications, and serverless environments.
It supports JWT-based authentication with refresh tokens, allowing for secure and stateless user sessions.
The service is designed to be extensible, allowing developers to customize authentication flows and integrate with
third-party identity providers.
Features can be enabled or disabled based on the specific needs of the application using Spring and Maven profiles.
Service supports event-driven architecture, allowing for easy integration with other services and systems. Domain events
are published to your desired message broker (e.g. Kafka, RabbitMQ) to notify other services of important events such as
user registration, password changes, and account lockouts. More details can be found in the
documentation [profiles](./doc/configuration/profiles.md).

Service is written using DDD principles with port-adapters architecture.

### Features

List of features can be found in the [features documentation](./doc/features/features.md).

### Customization

By default, the service is using the following profiles:

- `rest`
- `kafka`
- `mysql`

To enable or features please provide `spring.profile.active` property with proper profiles. The list can be found in
configuration documentation.
eg. To run the service with REST API, Kafka and MongoDB, use the following command:

```bash
mvn clean package 
java -jar auth-service.jar 

```

eg. To run the service with REST API, RabbitMQ and MongoDB, use the following command:

```bash
mvn clean package -Prest,rabbitmq,mongo
java -jar auth-service.jar --spring.profiles.active=rest,rabbitmq,mongo
```

It is important to build the jar with the correct profiles to ensure that all dependencies are included and not
necessary dependencies are excluded, otherwise the service may not work as expected.

## Development

- Locally
    - Compile the jar (`mvn package` or `mvn clean install`)
    - Fill up config file according to the documentation
    - Bring up the containers `docker compose up -d --build`
    - Run `java --jar target/auth.jar --Dspring.additional-config.location=config/config-dev.yml`
- With Docker
    - Compile the jar (`mvn package` or `mvn clean install`)
    - Fill up config file according to the documentation
    - Copy the file `docker/.env.dist` to `docker/.env` and propagate it with data
    - Bring up the database `docker compose up -d --build`