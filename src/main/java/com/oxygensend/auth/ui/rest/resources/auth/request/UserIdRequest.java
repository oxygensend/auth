package com.oxygensend.auth.ui.rest.resources.auth.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record UserIdRequest(@NotNull UUID id) {
}
