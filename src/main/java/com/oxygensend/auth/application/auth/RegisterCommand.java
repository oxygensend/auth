package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.application.identity.RegisterUserCommand;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
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

    public RegisterUserCommand toRegisterUserCommand(AccountActivationType accountActivationType) {
        return new RegisterUserCommand(
            email,
            username,
            rawPassword,
            roles,
            businessId,
            accountActivationType
        );
    }
}
