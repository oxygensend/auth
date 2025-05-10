# Auth mongodb

This module provides integration with MongoDB as a storage for user data and authentication information.

To use this module, you need to include the `mongo` profile when building the JAR. This will ensure that the necessary

# Config

It is required to provide the following configuration properties in `config.yml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/auth
```
