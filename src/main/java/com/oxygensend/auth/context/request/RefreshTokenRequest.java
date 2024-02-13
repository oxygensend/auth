package com.oxygensend.auth.context.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String token) {}

