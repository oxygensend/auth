package com.oxygensend.auth.port.adapter.in.rest.resources.auth.response;

import com.oxygensend.auth.port.Ports;
import org.springframework.context.annotation.Profile;

@Profile(Ports.REST)
public record AuthenticationResponse(String accessToken, String refreshToken) {
}
