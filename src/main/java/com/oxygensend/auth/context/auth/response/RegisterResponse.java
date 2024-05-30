package com.oxygensend.auth.context.auth.response;

import java.util.UUID;

public record RegisterResponse(UUID id, String accessToken, String refreshToken) {
}
