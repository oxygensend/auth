package com.oxygensend.auth.port.adapter.in.messaging.kafka;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ExcludeFromJacocoGeneratedReport
@Validated
@ConfigurationProperties(prefix = "kafka.consumer")
public record KafkaConsumerProperties(@NotEmpty String applicationId,
                                      @NotEmpty String bootstrapServers,
                                      @Positive Integer retryBackoffMs,
                                      @Positive Integer requestTimeoutMs,
                                      @Positive String maxPollIntervalMs,
                                      @Positive String maxPollRecords,
                                      @Positive Integer poolTimeoutMs,
                                      @Positive Integer connectionsMaxIdleMs,
                                      @Positive Integer consumerNumber,
                                      @NotEmpty String autoOffsetReset,
                                      @Valid KafkaSsl ssl,
                                      SecurityProtocol securityProtocol,
                                      String saslJaasConfig,
                                      String saslMechanism,
                                      @Valid Retry retry,
                                      @NotNull @Valid Topic topic) {

    public record KafkaSsl(@NotNull Boolean enabled,
                           String keyStore,
                           String keyStorePassword,
                           String keyStoreType,
                           String keyPassword,
                           String trustStore,
                           String trustStorePassword,
                           String trustStoreType) {
    }

    public record Retry(@Positive Integer maxRetries,
                        @Min(100) Integer backOffPeriod,
                        @Min(100) Integer backOffPeriodServiceUnavailable) {
    }

    public record Topic(@NotEmpty String registrationFailedTopic) {
    }


}
