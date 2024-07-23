package com.oxygensend.auth.application.auth.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record UserIdRequest(@NotNull UUID id) {
}
