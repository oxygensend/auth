package com.oxygensend.auth.context.auth.request;

import com.oxygensend.commons_jdk.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(@NotNull @ValidEmail String email,
                                    @NotBlank String password) {
}
