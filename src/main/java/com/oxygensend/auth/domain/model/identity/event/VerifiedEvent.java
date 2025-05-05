package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.UserId;

import common.domain.model.DomainEvent;

public record VerifiedEvent(UserId userId) implements DomainEvent {
    @Override
    public String key() {
        return userId.toString();
    }
}
