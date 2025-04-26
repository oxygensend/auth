package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import common.event.Event;

import java.util.UUID;

public record RegisterEvent(UUID userId,
                            String businessId,
                            String email,
                            AccountActivationType accountActivation) implements Event {

    @Override
    public String name() {
        return RegisterEvent.class.getSimpleName();
    }

    @Override
    public String key() {
        return userId.toString();
    }

}
