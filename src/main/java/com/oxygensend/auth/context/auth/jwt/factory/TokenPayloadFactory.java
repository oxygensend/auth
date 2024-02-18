package com.oxygensend.auth.context.auth.jwt.factory;

import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;

interface TokenPayloadFactory {

    TokenPayload createToken(Date exp, Date iat, User user);
    TokenPayload createToken(Claims claims);

    TokenType getType();
}
