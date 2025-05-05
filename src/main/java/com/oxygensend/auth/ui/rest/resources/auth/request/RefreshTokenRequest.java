package com.oxygensend.auth.ui.rest.resources.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String token) {}

