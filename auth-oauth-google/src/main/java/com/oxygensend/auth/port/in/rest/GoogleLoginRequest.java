package com.oxygensend.auth.port.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(@NotNull @NotBlank String code) {
}
