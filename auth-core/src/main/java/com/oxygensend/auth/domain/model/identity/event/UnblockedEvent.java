package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.UserId;

import com.oxygensend.common.domain.model.DomainEvent;

public record UnblockedEvent(UserId userId) implements DomainEvent {
}
