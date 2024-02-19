package com.oxygensend.auth.context.auth.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record UserIdRequest(@NotNull UUID id) {
}
