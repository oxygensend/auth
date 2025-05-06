package com.oxygensend.auth.port.adapter.in.rest.resources.auth.request;

import com.oxygensend.auth.port.Ports;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.annotation.Profile;

@Profile(Ports.REST)
public record RefreshTokenRequest(@NotBlank String token) {}

