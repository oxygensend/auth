package com.oxygensend.auth.port.adapter.out.messaging.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.oxygensend.common.domain.model.DomainEvent;
import com.oxygensend.common.domain.model.EventPublisher;

final class InternalEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(InternalEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public InternalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing event to internal event bus: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}
