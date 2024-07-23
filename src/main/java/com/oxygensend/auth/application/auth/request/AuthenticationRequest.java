package com.oxygensend.auth.application.auth.request;

import com.oxygensend.auth.infrastructure.validation.ValidIdentity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(@NotNull @ValidIdentity String identity,
                                    @NotBlank String password) {
}
