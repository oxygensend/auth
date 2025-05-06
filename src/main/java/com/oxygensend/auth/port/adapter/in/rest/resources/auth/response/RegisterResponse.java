package com.oxygensend.auth.port.adapter.in.rest.resources.auth.response;

import java.util.UUID;

public record RegisterResponse(UUID id, String accessToken, String refreshToken) {
}
