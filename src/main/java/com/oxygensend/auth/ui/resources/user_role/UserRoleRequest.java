package com.oxygensend.auth.ui.resources.user_role;

import com.oxygensend.auth.ui.validation.ValidRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
 record UserRoleRequest(@NotNull UUID userId, @ValidRole String role) {
}
