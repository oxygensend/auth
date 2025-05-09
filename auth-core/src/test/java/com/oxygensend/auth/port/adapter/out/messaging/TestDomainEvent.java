package com.oxygensend.auth.port.adapter.out.messaging;

import java.time.Instant;

import com.oxygensend.common.domain.model.DomainEvent;

public record TestDomainEvent(String id, Instant occurredOn, String aggregateType, String data)
    implements DomainEvent {

    public TestDomainEvent(String id, Instant occurredOn, String data) {
        this(id, occurredOn, null, data);
    }
}