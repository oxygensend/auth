package com.oxygensend.auth.domain.model.identity.event;

import com.oxygensend.auth.domain.model.AccountActivation;
import com.oxygensend.auth.domain.event.Event;

import java.util.UUID;

public record RegisterEvent(UUID userId,
                            String businessId,
                            String email,
                            AccountActivation accountActivation) implements Event {

    @Override
    public String name() {
        return RegisterEvent.class.getSimpleName();
    }

    @Override
    public String key() {
        return userId.toString();
    }

}
