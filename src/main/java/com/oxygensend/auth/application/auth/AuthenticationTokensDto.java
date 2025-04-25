package com.oxygensend.auth.application.auth;

public record AuthenticationTokensDto(String accessToken, String refreshToken) {
}
