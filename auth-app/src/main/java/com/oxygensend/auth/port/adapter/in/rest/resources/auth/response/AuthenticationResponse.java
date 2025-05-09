package com.oxygensend.auth.port.adapter.in.rest.resources.auth.response;


public record AuthenticationResponse(String accessToken, String refreshToken) {
}
