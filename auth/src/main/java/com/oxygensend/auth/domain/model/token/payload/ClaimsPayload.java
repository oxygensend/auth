package com.oxygensend.auth.domain.model.token.payload;

import io.jsonwebtoken.Claims;

public interface ClaimsPayload {
    Claims toClaims();

}
