# Default profiles

By default, the service uses the REST API profile. In the future, this may be replaced with gRPC or other protocols.

If you want to use the service with different profiles, you need to build the JAR with the appropriate Maven profiles.
This ensures that only the required dependencies are included, resulting in a lighter JAR file.

`mvn package -Pkafka,mongo`

⚠️ It's important to set the correct profiles and avoid incompatible combinations.
For instance, MySQL and MongoDB profiles should not be used together — they are not compatible.
The service is designed to detect such conflicts and will fail gracefully with an appropriate exception message.

# Profiles

- `rest` - REST API, enables REST API for authentication and user, roles management **ALWAYS enabled**
- `kafka` - Kafka, enables Kafka as a message broker for domain events and consumer for input events
- `rabbitmq` - RabbitMQ, enables RabbitMQ as a message broker for domain events and consumer for input events
- `mysql` - MySQL, enables MySQL as a storage
- `mongo` - MongoDB, enables MongoDB as a storage
- `eureka` - Eureka, enables Eureka as a service discovery registry
- 'oauth2-google' - Google OAuth2, enables Google OAuth2 for authentication