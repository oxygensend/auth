package com.oxygensend.auth.ui.rest.resources.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(@NotNull @NotBlank String token,
                                   @NotNull @NotBlank String newPassword) {
}
