package com.oxygensend.auth.port.adapter.in.messaging.kafka;

import com.oxygensend.auth.port.adapter.in.messaging.RegistrationFailedEvent;
import jakarta.ws.rs.ServiceUnavailableException;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import common.domain.model.DomainException;

@EnableConfigurationProperties(KafkaConsumerProperties.class)
@Configuration
public class KafkaConsumerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfiguration.class);
    private final KafkaConsumerProperties kafkaProperties;

    public KafkaConsumerConfiguration(KafkaConsumerProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    ConsumerFactory<String, RegistrationFailedEvent> registrationFailedEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties(), new StringDeserializer(),
                                                 errorHandlingDeserializer(RegistrationFailedEvent.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, RegistrationFailedEvent> registrationFailedEventConcurrentKafkaListenerContainerFactory(
        ConsumerFactory<String, RegistrationFailedEvent> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, RegistrationFailedEvent>();
        containerFactory.setConsumerFactory(consumerFactory);
        containerFactory.setCommonErrorHandler(errorHandler());
        return containerFactory;
    }


    private Map<String, Object> consumerProperties() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.applicationId());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.autoOffsetReset());
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.maxPollIntervalMs());
        configProps.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProperties.retryBackoffMs());
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.requestTimeoutMs());
        configProps.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, kafkaProperties.connectionsMaxIdleMs());
        configProps.putAll(secureConfigProperties());
        return configProps;
    }

    private Map<String, Object> secureConfigProperties() {
        Map<String, Object> secureProps = new HashMap<>();
        SecurityProtocol securityProtocol = kafkaProperties.securityProtocol();
        var kafkaSslSettings = kafkaProperties.ssl();
        if (!kafkaSslSettings.enabled()) {
            return secureProps;

        }
        secureProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol.name);
        if (securityProtocol == SecurityProtocol.SSL || securityProtocol == SecurityProtocol.SASL_SSL) {
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaSslSettings.trustStore());
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaSslSettings.trustStorePassword());
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, kafkaSslSettings.trustStoreType());
            secureProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, kafkaSslSettings.keyStore());
            secureProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, kafkaSslSettings.keyStorePassword());
            secureProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, kafkaSslSettings.keyStoreType());
            secureProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, kafkaSslSettings.keyPassword());
        }

        if (securityProtocol == SecurityProtocol.SASL_SSL) {
            secureProps.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaProperties.saslJaasConfig());
            secureProps.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.saslMechanism());
        }
        return secureProps;
    }

    private CommonErrorHandler errorHandler() {
        var errorHandler = new DefaultErrorHandler(this::consumerRecordRecoverer);
        errorHandler.addRetryableExceptions(ServiceUnavailableException.class, DomainException.class);
        errorHandler.setBackOffFunction((consumerRecord, exception) -> backOffConfig(exception));
        return errorHandler;
    }

    private BackOff backOffConfig(Exception exception) {
        if (exception instanceof DomainException) {
            return new FixedBackOff(kafkaProperties.retry().backOffPeriod(), kafkaProperties.retry().maxRetries());
        } else if (exception instanceof ServiceUnavailableException) {
            var backOff = new FixedBackOff();
            backOff.setInterval(kafkaProperties.retry().backOffPeriodServiceUnavailable());
            return backOff;
        }

        return new FixedBackOff(0, 0);
    }

    private void consumerRecordRecoverer(ConsumerRecord<?, ?> consumerRecord, Exception exception) {
        if (isDeserializationException(exception)) {
            DeserializationException deserializationException = (DeserializationException) exception;
            String brokenMessage = new String(deserializationException.getData());
            LOGGER.info("Skipping record with topic: {}, partition: {}, offset: {}, broken message: {}",
                        consumerRecord.topic(),
                        consumerRecord.partition(), consumerRecord.offset(), brokenMessage);
        } else {
            LOGGER.error("Consumer record exception - topic: {}, partition: {}, offset: {}, cause: {}",
                         consumerRecord.topic(),
                         consumerRecord.partition(), consumerRecord.offset(), exception.getMessage(), exception);
        }
    }

    private boolean isDeserializationException(Exception exception) {
        return exception instanceof DeserializationException;
    }

    private <T> ErrorHandlingDeserializer<T> errorHandlingDeserializer(Class<T> clazz) {
        return new ErrorHandlingDeserializer<>(new JsonDeserializer<>(clazz, false));
    }
}
