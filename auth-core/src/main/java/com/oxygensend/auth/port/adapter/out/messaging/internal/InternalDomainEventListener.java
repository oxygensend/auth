package com.oxygensend.auth.port.adapter.out.messaging.internal;

import com.oxygensend.common.domain.model.DomainEvent;
import org.springframework.context.event.EventListener;

public class InternalDomainEventListener {

    @EventListener
    public void handleEvent(DomainEvent event) {
        // Handle the event here
        System.out.println("Handling event: " + event);
    }
}
