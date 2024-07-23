package com.oxygensend.auth.application.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailRequest(@NotNull @NotBlank String token) {
}
