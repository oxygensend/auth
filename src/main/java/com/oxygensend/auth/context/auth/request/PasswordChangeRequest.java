package com.oxygensend.auth.context.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeRequest(@NotNull @NotBlank String newPassword,
                                    @NotNull @NotBlank String oldPassword) {
}
