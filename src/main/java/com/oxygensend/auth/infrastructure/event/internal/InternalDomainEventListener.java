package com.oxygensend.auth.infrastructure.event.internal;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import common.domain.model.DomainEvent;

@Component
@Profile("!KAFKA")
public class InternalDomainEventListener {

    @EventListener
    public void handleEvent(DomainEvent event) {
        // Handle the event here
        System.out.println("Handling event: " + event);
    }
}
