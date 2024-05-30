package com.oxygensend.auth.config.properties;


import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("kafka")
public record KafkaProperties(@NotNull String bootstrapServers,
                              @NotNull SecurityProtocol securityProtocol,
                              String saslJaasConfig,
                              String saslMechanism,
                              KafkaSsl ssl,
                              @NotNull Integer retries,
                              @NotNull Integer retryBackoffInMs,
                              String stateDir) {

    public record KafkaSsl(@NotNull Boolean enabled,
                           String keyStore,
                           String keyStorePassword,
                           String keyStoreType,
                           String keyPassword,
                           String trustStore,
                           String trustStorePassword,
                           String trustStoreType) {


    }
}
