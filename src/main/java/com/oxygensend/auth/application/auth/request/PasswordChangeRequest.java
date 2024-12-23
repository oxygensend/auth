package com.oxygensend.auth.application.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeRequest(@NotNull @NotBlank String newPassword,
                                    @NotNull @NotBlank String oldPassword) {
}
