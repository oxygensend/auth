package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;

import common.domain.model.DomainEvent;

public record AddedRoleEvent(UserId userId, Role role) implements DomainEvent {
    @Override
    public String key() {
        return userId.toString();
    }
}
