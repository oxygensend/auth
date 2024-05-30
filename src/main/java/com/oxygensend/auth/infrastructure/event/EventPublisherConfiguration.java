package com.oxygensend.auth.infrastructure.event;

import com.oxygensend.auth.domain.event.Event;
import com.oxygensend.auth.domain.event.EventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class EventPublisherConfiguration {

    @Bean
    @ConditionalOnProperty(name = "auth.settings.event-broker", havingValue = "internal")
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnProperty(name = "auth.settings.event-broker", havingValue = "kafka")
    EventPublisher kafkaEventPublisher(KafkaTemplate<String, Event> kafkaTemplate) {
        return new KafkaEventPublisher(kafkaTemplate);
    }
}
