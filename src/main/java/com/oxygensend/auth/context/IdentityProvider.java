package com.oxygensend.auth.context;

import com.oxygensend.auth.config.IdentityType;
import com.oxygensend.auth.domain.User;

public class IdentityProvider {
    private final IdentityType identityType;

    public IdentityProvider(IdentityType identityType) {
        this.identityType = identityType;
    }

    public String getIdentity(User user) {
        return switch (identityType) {
            case USERNAME -> user.username();
            case EMAIL -> user.email();
        };
    }


    public IdentityType getIdentityType() {
        return identityType;
    }

}
