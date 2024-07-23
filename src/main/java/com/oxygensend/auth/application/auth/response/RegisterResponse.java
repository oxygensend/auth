package com.oxygensend.auth.application.auth.response;

import java.util.UUID;

public record RegisterResponse(UUID id, String accessToken, String refreshToken) {
}
