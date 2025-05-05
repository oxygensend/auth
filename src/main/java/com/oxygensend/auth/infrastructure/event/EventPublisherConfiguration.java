package com.oxygensend.auth.infrastructure.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import common.domain.model.DomainEvent;
import common.event.EventPublisher;

@Configuration
public class EventPublisherConfiguration {

    @Bean
    @ConditionalOnProperty(name = "auth.settings.event-broker", havingValue = "internal")
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnProperty(name = "auth.settings.event-broker", havingValue = "kafka")
    EventPublisher kafkaEventPublisher(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        return new KafkaEventPublisher(kafkaTemplate);
    }
}
