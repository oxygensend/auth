package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.token.payload.TokenPayload;

public interface TokenService {

    String createToken(TokenPayload payload);

    TokenPayload parseToken(String token, TokenType type);
}
