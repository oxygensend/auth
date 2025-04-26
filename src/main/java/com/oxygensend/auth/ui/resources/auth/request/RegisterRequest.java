package com.oxygensend.auth.ui.resources.auth.request;

import com.oxygensend.auth.application.auth.RegisterCommand;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.ui.validation.ValidRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

public record RegisterRequest(@NotNull String email,
                              String username,
                              @Size(min = 4, max = 64) @NotBlank String password,
                              @NotEmpty Set<@ValidRole String> roles,
                              String businessId) {

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
            new BusinessId(businessId())
        );
    }

}
