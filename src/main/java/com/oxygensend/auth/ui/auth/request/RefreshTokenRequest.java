package com.oxygensend.auth.ui.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String token) {}

