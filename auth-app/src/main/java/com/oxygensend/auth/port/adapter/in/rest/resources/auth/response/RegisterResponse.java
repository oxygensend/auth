package com.oxygensend.auth.port.adapter.in.rest.resources.auth.response;

import com.oxygensend.auth.port.Ports;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Profile(Ports.REST)
public record RegisterResponse(UUID userId, String accessToken, String refreshToken) {
}
