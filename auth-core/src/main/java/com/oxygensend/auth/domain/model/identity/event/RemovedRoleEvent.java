package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;

import com.oxygensend.common.domain.model.DomainEvent;

public record RemovedRoleEvent(UserId userId, Role role) implements DomainEvent {

}
