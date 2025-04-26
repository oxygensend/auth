package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;

import java.util.Set;

public record RegisterCommand(
    EmailAddress email,
    Username username,
    String rawPassword,
    Set<Role> roles,
    BusinessId businessId
) {
}
