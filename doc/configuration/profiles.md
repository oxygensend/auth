# Default profiles 
By default, the service is using the following profiles:
- `rest` 
- `kafka` 
- `mysql` 

If you want to use the service with different provices, you need to set them in the `spring.profiles.active` property.
It is important to set the profiles and not to mix them up, for eg.
Mysql and Mongodb shouldn't be used together, they are not compatible. Service is prepared for such a case and will fail with proper exception message.

# Profiles
- `rest` - REST API, enables REST API for authentication and user, roles management
- `kafka` - Kafka, enables Kafka as a message broker for domain events and consumer for input events
- `rabbitmq` - RabbitMQ, enables RabbitMQ as a message broker for domain events and consumer for input events
- `mysql` - MySQL, enables MySQL as a storage 
- `mongodb` - MongoDB, enables MongoDB as a storage
- `eureka` - Eureka, enables Eureka as a service discovery registry
- 'oauth2-google' - Google OAuth2, enables Google OAuth2 for authentication