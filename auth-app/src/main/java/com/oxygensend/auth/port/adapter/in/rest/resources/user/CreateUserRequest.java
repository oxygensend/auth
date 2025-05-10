package com.oxygensend.auth.port.adapter.in.rest.resources.user;

import com.oxygensend.auth.application.identity.RegisterUserCommand;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;


public record CreateUserRequest(@NotNull @NotBlank String email,
                                @NotNull @NotBlank String username,
                                @NotNull @NotBlank String password,
                                @NotNull @NotEmpty Set<String> roles,
                                @NotNull @NotBlank String businessId
) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(
            new EmailAddress(email()),
            new Username(username()),
            password(),
            roles().stream().map(Role::new).collect(Collectors.toSet()),
            new BusinessId(businessId()),
            AccountActivationType.NONE
        );
    }
}
