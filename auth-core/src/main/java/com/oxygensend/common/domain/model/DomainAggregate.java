package com.oxygensend.common.domain.model;

import java.util.ArrayList;
import java.util.Collection;

public abstract class DomainAggregate {
    protected final Collection<DomainEvent> events = new ArrayList<>();

    public Collection<DomainEvent> events() {
        return events;
    }

    protected void addEvent(DomainEvent event) {
        events.add(event);
    }
}
