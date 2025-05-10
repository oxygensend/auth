package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import io.jsonwebtoken.Claims;
import java.util.Date;

interface TokenPayloadFactory {

    TokenPayload createPayload(Date exp, Date iat, TokenSubject subject);
    TokenPayload createPayload(Claims claims);
    TokenType getType();
}
