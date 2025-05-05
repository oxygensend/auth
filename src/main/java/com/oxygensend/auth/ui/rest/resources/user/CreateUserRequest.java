package com.oxygensend.auth.ui.rest.resources.user;

import com.oxygensend.auth.application.identity.CreateUserCommand;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public record CreateUserRequest(@NotEmpty String identity,
                                @NotEmpty String userName,
                                @NotNull @NotEmpty String email,
                                @NotEmpty Set<String> roles,
                                boolean verified,
                                @NotEmpty String businessId,
                                @NotEmpty String password) {

    public CreateUserCommand toCommand() {
        return new CreateUserCommand(
            new EmailAddress(email()),
            new Username(userName()),
            password(),
            roles().stream().map(Role::new).collect(Collectors.toSet()),
            verified(),
            new BusinessId(businessId())
        );
    }
}
