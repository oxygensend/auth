package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record RefreshTokenRequest(@NotNull @NotBlank String token) {
}

