package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import com.oxygensend.auth.port.Ports;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Profile(Ports.REST)
record UserRoleRequest(@NotNull UUID userId, @NotBlank String role) {
}
