package com.oxygensend.auth.domain.model.identity;

import java.util.Set;

public record UserDescriptor(UserId userId,
                             Username username,
                             EmailAddress email,
                             BusinessId businessId,
                             Set<Role> roles, boolean verified) {

    public UserDescriptor(User user) {
        this(user.id(), user.username(), user.email(), user.businessId(), user.roles(), user.isVerified());
    }
}
