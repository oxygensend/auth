package com.oxygensend.auth.application.jwt.factory;

import com.oxygensend.auth.application.jwt.payload.TokenPayload;
import com.oxygensend.auth.application.jwt.payload.RefreshTokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
final class RefreshTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new RefreshTokenPayload(
                user.id(),
                iat,
                exp
        );
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new RefreshTokenPayload(
                UUID.fromString(claims.getSubject()),
                claims.getIssuedAt(),
                claims.getExpiration()
        );
    }

    @Override
    public TokenType getType() {
        return TokenType.REFRESH;
    }
}
