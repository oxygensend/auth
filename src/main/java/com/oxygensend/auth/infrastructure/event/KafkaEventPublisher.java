package com.oxygensend.auth.infrastructure.event;

import common.event.Event;
import common.event.EventPublisher;
import common.event.EventWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
final class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Override
    public void publish(EventWrapper eventWrapper) {
        log.info("Publishing event to Kafka: {}", eventWrapper);
        var event = eventWrapper.event();
        kafkaTemplate.send(eventWrapper.destination(), event.key(), event);
    }
}
