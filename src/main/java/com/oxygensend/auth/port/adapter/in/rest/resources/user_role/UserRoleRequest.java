package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.in.rest.validation.ValidRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Profile(Ports.REST)
record UserRoleRequest(@NotNull UUID userId, @ValidRole String role) {
}
