package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


record UserRoleRequest(@NotNull UUID userId, @NotBlank String role) {
}
