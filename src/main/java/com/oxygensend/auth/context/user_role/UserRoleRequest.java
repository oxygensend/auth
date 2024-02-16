package com.oxygensend.auth.context.user_role;

import com.oxygensend.auth.domain.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleRequest(@NotNull UUID userId, UserRole role) {
}
