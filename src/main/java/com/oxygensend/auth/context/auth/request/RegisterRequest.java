package com.oxygensend.auth.context.auth.request;

import com.oxygensend.auth.domain.UserRole;
import com.oxygensend.auth.infrastructure.validation.ValidIdentity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record RegisterRequest(@Size(min = 2, max = 64) @NotBlank String firstName,
                              @Size(min = 2, max = 64) @NotBlank String lastName,
                              @NotNull @ValidIdentity String identity,
                              @Size(min = 4, max = 64) @NotBlank String password,
                              Set<UserRole> roles) {

    public RegisterRequest(String firstName, String lastName, String identity, String password) {
        this(firstName, lastName, identity, password, Set.of(UserRole.ROLE_USER));
    }

    public RegisterRequest {
        if (roles == null || roles.isEmpty()) {
            roles = Set.of(UserRole.ROLE_USER);
        }
    }

}
