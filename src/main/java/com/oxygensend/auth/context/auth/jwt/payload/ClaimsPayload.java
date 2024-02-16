package com.oxygensend.auth.context.auth.jwt.payload;

import io.jsonwebtoken.Claims;

public interface ClaimsPayload {
    Claims toClaims();

}
