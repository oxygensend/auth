package com.oxygensend.auth.infrastructure.event;

import com.oxygensend.auth.domain.event.Event;
import com.oxygensend.auth.domain.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
final class InternalEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Event event) {
        log.info("Publishing internal event: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}
