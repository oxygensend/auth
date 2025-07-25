package com.oxygensend.auth.port.in.rest;


public record AuthenticationResponse(String accessToken, String refreshToken) {
}
