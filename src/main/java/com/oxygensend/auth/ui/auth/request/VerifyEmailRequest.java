package com.oxygensend.auth.ui.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailRequest(@NotNull @NotBlank String token) {
}
