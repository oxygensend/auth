package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import com.oxygensend.auth.port.adapter.in.rest.validation.ValidRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
 record UserRoleRequest(@NotNull UUID userId, @ValidRole String role) {
}
