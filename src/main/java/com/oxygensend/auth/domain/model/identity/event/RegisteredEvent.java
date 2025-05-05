package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;

import common.domain.model.DomainEvent;

public record RegisteredEvent(UserId userId,
                              BusinessId businessId,
                              EmailAddress email,
                              Username username,
                              AccountActivationType accountActivationType) implements DomainEvent {

    public RegisteredEvent(User user) {
        this(user.id(),
             user.businessId(),
             user.credentials().email(),
             user.credentials().username(),
             user.accountActivationType());
    }

    @Override
    public String id() {
        return userId.value().toString();
    }
}
