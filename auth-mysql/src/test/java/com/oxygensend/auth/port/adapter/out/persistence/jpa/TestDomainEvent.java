package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.common.domain.model.DomainEvent;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;


public record TestDomainEvent(String id, Instant occurredOn, String aggregateType, String data)
    implements DomainEvent {

    public TestDomainEvent(String id, Instant occurredOn, String data) {
        this(id, occurredOn, null, data);
    }

    public TestDomainEvent(){
        this(UUID.randomUUID().toString(), Instant.now(), "Test", "test data");
    }
}