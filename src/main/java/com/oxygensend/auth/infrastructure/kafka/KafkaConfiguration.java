package com.oxygensend.auth.infrastructure.kafka;

import com.oxygensend.auth.infrastructure.app_config.properties.KafkaProperties;
import com.oxygensend.auth.infrastructure.app_config.properties.SettingsProperties;
import common.event.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ConditionalOnProperty(name = "auth.settings.event-broker", havingValue = "kafka")
@EnableConfigurationProperties(KafkaProperties.class)
@Configuration
public class KafkaConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaConfiguration.class);
    private final KafkaProperties kafkaProperties;
    private final SettingsProperties settingsProperties;

    KafkaConfiguration(KafkaProperties kafkaProperties, SettingsProperties settingsProperties) {
        this.kafkaProperties = kafkaProperties;
        this.settingsProperties = settingsProperties;
        verifyTopicsExistence();
    }

    @Bean
    public KafkaTemplate<String, Event> kafkaTemplate() {
        var producerFactory = new DefaultKafkaProducerFactory<String, Event>(configProperties());
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setProducerListener(new KafkaProducerListener<>());
        return kafkaTemplate;
    }

    private Map<String, Object> configProperties() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProperties.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.retries());
        configProperties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProperties.retries());
        configProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        if (kafkaProperties.ssl().enabled()) {
            configProperties.putAll(secureConfigProperties());
        }
        return configProperties;
    }

    private Map<String, Object> secureConfigProperties() {
        Map<String, Object> secureProps = new HashMap<>();

        if (kafkaProperties.securityProtocol() == SecurityProtocol.SSL || kafkaProperties.securityProtocol() == SecurityProtocol.SASL_SSL) {
            secureProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaProperties.securityProtocol().name);

            var sslProps = kafkaProperties.ssl();
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslProps.trustStore());
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslProps.trustStorePassword());
            secureProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, sslProps.trustStoreType());
            secureProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslProps.keyStore());
            secureProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslProps.keyStorePassword());
            secureProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, sslProps.keyStoreType());
            secureProps.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, sslProps.keyPassword());
        }

        if (kafkaProperties.securityProtocol() == SecurityProtocol.SASL_SSL) {
            secureProps.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaProperties.saslJaasConfig());
            secureProps.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.saslMechanism());
        }
        return secureProps;

    }

    private void verifyTopicsExistence() {
        Map<String, Object> props = secureConfigProperties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        try (Admin adminClient = AdminClient.create(props)) {
            var topics = getAllTopics();
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(topics);
            describeTopicsResult.topicNameValues().forEach((key, value) -> {
                try {
                    value.get();
                } catch (InterruptedException | ExecutionException exception) {
                    LOGGER.error("Error during topic description retrieval", exception);
                    throw new InvalidConfigurationException("Failed to obtain topic description: " + key);
                }
            });
        }
    }

    private Set<String> getAllTopics() {
        return Set.of(settingsProperties.signIn().registerEventTopic());
    }

}
