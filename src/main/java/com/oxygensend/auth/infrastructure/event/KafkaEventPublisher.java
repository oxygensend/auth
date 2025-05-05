package com.oxygensend.auth.infrastructure.event;

import common.event.Event;
import common.event.EventPublisher;
import common.event.EventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

final class KafkaEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, Event> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Event> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(EventWrapper eventWrapper) {
        log.info("Publishing event to Kafka: {}", eventWrapper);
        var event = eventWrapper.event();
        kafkaTemplate.send(eventWrapper.destination(), event.key(), event);
    }
}
