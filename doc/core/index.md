This repository provides a modular authentication service (`auth-app` module) designed primarily for microservices
architecture, but
flexible enough to be integrated into any backend application — including monoliths.

Each module is self-contained and can be used as a dependency to extend the functionality of your application. Whether
you're working in a microservices environment or a monolithic architecture, you can integrate and customize the
components as needed.

## auth-core

- The core module of the authentication service. It contains the main domain logic and application services for
  authentication, authorization and user management.

- Designed to be framework-agnostic and can be integrated with any backend.

- All other modules are optional extensions that depend on auth-core (e.g., storage implementations, message brokers).

- By default, it uses Spring Application Events to publish domain events — allowing you to listen and react to them in
  your application.

## Integration

To use or extend the functionality in your application:

- Add the desired module (e.g., auth-core) as a dependency to your project via Maven or Gradle.

```xml

<dependency>
  <groupId>com.example</groupId>
  <artifactId>auth-core</artifactId>
  <version>${project.version}</version>
</dependency>
```

- Customize or extend the logic as needed.