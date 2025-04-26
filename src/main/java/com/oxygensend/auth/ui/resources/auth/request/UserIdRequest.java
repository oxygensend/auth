package com.oxygensend.auth.ui.resources.auth.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record UserIdRequest(@NotNull UUID id) {
}
