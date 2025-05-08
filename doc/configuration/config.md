The service is fully customizable based on your needs, in order for it to work properly, you need to set the following configuration and provide it to your Spring application, for example: `java --jar target/notifications.jar --Dspring.additional-config.location=config/config-dev.yml`
```yaml
spring:
  profiles:
    active: # active functionalities separated by commas, possible values: (rest,mail,sms,whatsapp,internal,kafka), eg. rest,mail,kafka 
  data:
    mongodb: # Only if MONGODB PROFILE IS ENABLED, mongodb configuration
      uri: # database uri

eureka: # ONLY if EUREKA PROFILE IS ENABLED, eureka configuration
  client:
    enabled: true # if eureka is used as a service discovery registry
  
server:
  tomcat:
  accesslog: # enable access log default: true
    max-days: 5
    directory: log/access
    enabled: true
    
security:
  token:
    expiration: # Duration format, eg. 1h,
      access: 3600s # access token expiration time default: 3600s
      refresh: 86400s # refresh token expiration time default: 86400s
      password-reset: 2d # password reset token expiration time  default: 2d
      email-verification: 5d # email verification token expiration time default: 5d
    secretKey: # secret key for JWT token generation
    
auth:
  settings:
    sign-in:
      account-activation: none # Should the account be activated after registration, possible values: none, email-verify, password-change, default: none
    roles: # roles that will be used for authentication for a non-admin user
       - ROLE_USER
    admin-roles: # roles that will be used for authentication for an admin user
       - ROLE_ADMIN
    login-type: email # login type for authentication, possible values: email, username, default: email


kafka: # ONLY if KAFKA PROFILE IS ENABLED, kafka configuration
  consumer:
    application-id: # application id eg. notification-service
    bootstrap-servers: # bootsrap server eg. localhost:29092 
    security-protocol: # security protcol eg. PLAINTEXT
    sasl-jaas-config:  # sasl eg. org.apache.kafka.common.security.scram.ScramLoginModule required username="dev" password="dev123" # sasl
    retry:
      max-retries: 5 # max retries default: 5
      back-off-period: 500 # back off period default: 500
      backoff-period-service-unavailable: 5000 #  default 5000
    retry-backoff-ms: 1000 # default 1000
    ssl:
      enabled: false # default false
    auto-offset-reset: earliest # default earliest
    topic:
      registration-failed-topic: registration-failed
  producer:
    producer:
    bootstrap-servers: localhost:29092
    security-protocol: PLAINTEXT
    writeTopicName: auth-service
    # etc.

```