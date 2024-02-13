package com.oxygensend.auth.context.jwt.factory;

import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new AccessTokenPayload(
                user.firstName(),
                user.lastName(),
                user.email(),
                iat,
                exp);
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new AccessTokenPayload(
                (String) claims.get("firstName"),
                (String) claims.get("lastName"),
                claims.getSubject(),
                claims.getIssuedAt(),
                claims.getExpiration());
    }

    @Override
    public TokenType getType() {
        return TokenType.ACCESS;
    }
}
