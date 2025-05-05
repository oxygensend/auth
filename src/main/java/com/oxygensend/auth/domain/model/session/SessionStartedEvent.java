package com.oxygensend.auth.domain.model.session;

import com.oxygensend.auth.domain.model.identity.UserId;

import common.domain.model.DomainEvent;

public record SessionStartedEvent(SessionId sessionId,
                                  UserId userId) implements DomainEvent {
    @Override
    public String key() {
        return sessionId.toString();
    }
}
