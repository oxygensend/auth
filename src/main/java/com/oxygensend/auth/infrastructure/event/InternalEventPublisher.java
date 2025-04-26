package com.oxygensend.auth.infrastructure.event;

import common.event.EventPublisher;
import common.event.EventWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@RequiredArgsConstructor()
final class InternalEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(EventWrapper eventWrapper) {
        log.info("Publishing event to internal event bus: {}", eventWrapper);
        applicationEventPublisher.publishEvent(eventWrapper.event());
    }
}
