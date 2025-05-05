package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.UserId;

import common.domain.model.DomainEvent;

public record BlockedEvent(UserId userId) implements DomainEvent {
    @Override
    public String id() {
        return userId.value().toString();
    }
}
