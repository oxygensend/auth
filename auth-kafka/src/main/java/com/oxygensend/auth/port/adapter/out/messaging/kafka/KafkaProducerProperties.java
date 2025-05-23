package com.oxygensend.auth.port.adapter.out.messaging.kafka;


import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ExcludeFromJacocoGeneratedReport
@Validated
@ConfigurationProperties("kafka.producer")
public record KafkaProducerProperties(@NotNull String bootstrapServers,
                                      @NotNull SecurityProtocol securityProtocol,
                                      String saslJaasConfig,
                                      String saslMechanism,
                                      KafkaSsl ssl,
                                      @NotNull Integer retries,
                                      @NotNull Integer retryBackoffInMs,
                                      String stateDir,
                                      @NotEmpty String writeTopicName) {

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
