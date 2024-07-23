package com.oxygensend.auth.application.user_role;

import com.oxygensend.auth.infrastructure.validation.ValidRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleRequest(@NotNull UUID userId, @ValidRole String role) {
}
