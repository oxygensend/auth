# Auth mysql

This module provides integration with MySQL as a storage for user data and authentication information.

To use this module, you need to include the `mysql` profile when building the JAR. This will ensure that the necessary

# Config

It is required to provide the following configuration properties in `config.yml`:

```yaml
spring:
  datasource:
      url: jdbc:mysql://localhost:3309/auth
      username: root
      password: root
```
