package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserName;
import com.oxygensend.auth.ui.auth.request.RegisterRequest;

import java.util.Set;
import java.util.stream.Collectors;

public record RegisterCommand(
    EmailAddress email,
    UserName userName,
    String rawPassword,
    Set<Role> roles,
    BusinessId businessId
) {
    public static RegisterCommand fromRequest(RegisterRequest request) {
        return new RegisterCommand(
            new EmailAddress(request.email()),
            new UserName(request.username()),
            request.password(),
            request.roles().stream().map(Role::new).collect(Collectors.toSet()),
            new BusinessId(request.businessId())
        );
    }
}
