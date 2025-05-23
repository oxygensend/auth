package com.oxygensend.auth.port.adapter.out.messaging.kafka;

import com.oxygensend.common.domain.model.DomainEvent;
import com.oxygensend.common.domain.model.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

final class KafkaEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing event to Kafka: {}", event);

        var messageBuilder = MessageBuilder
            .withPayload(event)
            .setHeader(KafkaHeaders.KEY, event.id())
            .setHeader("aggregateType", event.aggregateType())
            .setHeader("eventType", event.getClass().getSimpleName());

        kafkaTemplate.send(messageBuilder.build());
    }
}
