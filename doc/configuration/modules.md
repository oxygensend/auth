# Modules

By default, the auth service uses the REST API moule. In the future, this may be replaced with gRPC or other protocols
implemented as a profiles.

Auth application is designed to be modular, allowing you to enable or disable specific functionalities based on your
needs.
To enable a module, you need to build the JAR with the appropriate Maven profiles to enable specific modules.
This ensures that only the required dependencies are included, resulting in a lighter JAR file.

`mvn package -Pkafka,mongo`

⚠️ It's important to set the correct profiles and avoid incompatible combinations.
For instance, MySQL and MongoDB profiles should not be used together — they are not compatible.
The service is designed to detect such conflicts and will fail gracefully with an appropriate exception message.

# Modules

Profile name is the same as the module name, for example, to enable Kafka and MongoDB, use `-Pkafka,mongo`.

- [kafka](../../auth-kafka/README.md) - Kafka, enables Kafka as a message broker for domain events and consumer for
  input events
- [mysql](../../auth-mysql/README.md) - MySQL, enables MySQL as a storage
- [mongo](../../auth-mongodb/README.md) - MongoDB, enables MongoDB as a storage
- `eureka` - Eureka, enables Eureka as a service discovery registry

## IN PROGRESS
- `rabbitmq` - RabbitMQ, enables RabbitMQ as a message broker for domain events and consumer for input events
- 'oauth2-google' - Google OAuth2, enables Google OAuth2 for authentication
