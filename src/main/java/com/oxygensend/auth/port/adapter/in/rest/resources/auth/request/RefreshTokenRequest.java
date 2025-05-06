package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String token) {}

