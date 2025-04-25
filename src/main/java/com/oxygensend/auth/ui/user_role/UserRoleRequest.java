package com.oxygensend.auth.ui.user_role;

import com.oxygensend.auth.infrastructure.validation.ValidRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
 record UserRoleRequest(@NotNull UUID userId, @ValidRole String role) {
}
