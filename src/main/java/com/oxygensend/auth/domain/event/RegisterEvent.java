package com.oxygensend.auth.domain.event;

import com.oxygensend.auth.domain.AccountActivation;
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
