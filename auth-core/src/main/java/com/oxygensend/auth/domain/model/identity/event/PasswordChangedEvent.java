package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.UserId;

import com.oxygensend.common.domain.model.DomainEvent;

public record PasswordChangedEvent(UserId userId, EmailAddress email) implements DomainEvent {

}
