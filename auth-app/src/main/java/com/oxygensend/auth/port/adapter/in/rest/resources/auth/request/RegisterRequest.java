package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import com.oxygensend.auth.application.auth.RegisterCommand;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;


public record RegisterRequest(@NotNull @NotBlank String email,
                              @NotNull @NotBlank String username,
                              @Size(min = 4, max = 64) @NotBlank String password,
                               Set<String> roles,
                              String businessId) {

    public RegisterRequest(String email, String username, String password, Set<String> roles) {
        this(email, username, password, roles, null);
    }

    public RegisterRequest(String identity, String password) {
        this(identity, password, null, Set.of("ROLE_USER"), null);
    }

    public RegisterRequest {
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("ROLE_USER");
        }
    }

    public RegisterCommand toCommand() {
        return new RegisterCommand(
            new EmailAddress(email()),
            new Username(username()),
            password(),
            roles().stream().map(Role::new).collect(Collectors.toSet()),
           businessId != null ? new BusinessId(businessId()) : null
        );
    }

}
