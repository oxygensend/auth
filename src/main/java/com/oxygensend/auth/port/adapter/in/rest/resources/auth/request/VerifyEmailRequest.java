package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import com.oxygensend.auth.port.Ports;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;

@Profile(Ports.REST)
public record VerifyEmailRequest(@NotNull @NotBlank String token) {
}
