package com.oxygensend.auth.application.jwt.payload;

import io.jsonwebtoken.Claims;

public interface ClaimsPayload {
    Claims toClaims();

}
