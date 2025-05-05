package com.oxygensend.auth.ui.rest.resources.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(@NotNull String login,
                                    @NotBlank String password) {
}
