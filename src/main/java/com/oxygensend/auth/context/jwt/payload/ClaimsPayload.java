package com.oxygensend.auth.context.jwt.payload;

import io.jsonwebtoken.Claims;

public interface ClaimsPayload {
    Claims toClaims();

}
