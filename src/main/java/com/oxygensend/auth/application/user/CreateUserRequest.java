package com.oxygensend.auth.application.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record CreateUserRequest(@NotNull UUID id,
                                @NotEmpty String identity,
                                @NotEmpty Set<String> roles,
                                boolean verified,
                                @NotEmpty String businessId,
                                @NotEmpty String password) {
}
