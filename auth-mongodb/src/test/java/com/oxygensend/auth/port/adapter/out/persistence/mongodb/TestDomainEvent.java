package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.common.domain.model.DomainEvent;

import java.time.Instant;


public record TestDomainEvent(String id, Instant occurredOn, String aggregateType, String data)
    implements DomainEvent {

    public TestDomainEvent(String id, Instant occurredOn, String data) {
        this(id, occurredOn, null, data);
    }
}