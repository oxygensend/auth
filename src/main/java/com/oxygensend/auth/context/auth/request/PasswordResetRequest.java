package com.oxygensend.auth.context.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(@NotNull @NotBlank String token,
                                   @NotNull @NotBlank String newPassword) {
}
