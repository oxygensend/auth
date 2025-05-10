# Auth kafka

This module provides integration with Kafka as a message broker for domain events and consumer for input events.

To use this module, you need to include the `kafka` profile when building the JAR. This will ensure that the necessary


# Config
It is required to provide the following configuration properties in `config.yml`:

```yaml
kafka:
  producer:
    bootstrap-servers: localhost:29092
    security-protocol: PLAINTEXT
    writeTopicName: outsource-me-api-user-registered
  consumer:
    application-id: auth-service
    bootstrap-servers: localhost:29092
    security-protocol: PLAINTEXT
    topic:
      registration-failed-topic: outsource-me-api-registration-failed
```