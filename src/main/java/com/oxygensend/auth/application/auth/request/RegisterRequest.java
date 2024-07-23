package com.oxygensend.auth.application.auth.request;

import com.oxygensend.auth.infrastructure.validation.ValidIdentity;
import com.oxygensend.auth.infrastructure.validation.ValidRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record RegisterRequest(@NotNull @ValidIdentity String identity,
                              @Size(min = 4, max = 64) @NotBlank String password,
                              @NotEmpty Set<@ValidRole String> roles,
                              String businessId) {

    public RegisterRequest(String identity, String password) {
        this(identity, password, Set.of("ROLE_USER"), null);
    }

    public RegisterRequest {
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("ROLE_USER");
        }
    }

}
