package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.RefreshTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;

import com.oxygensend.auth.domain.model.identity.UserId;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
final class RefreshTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createPayload(Date exp, Date iat, TokenSubject subject) {
        if (!(subject instanceof RefreshTokenSubject)) {
            throw new IllegalArgumentException("Invalid token subject type: " + subject.getClass());
        }
        return new RefreshTokenPayload(
                subject.userId(),
                iat,
                exp
        );
    }



    @Override
    public TokenPayload createPayload(Claims claims) {
        return new RefreshTokenPayload(
            new UserId(UUID.fromString(claims.getSubject())),
            claims.getIssuedAt(),
            claims.getExpiration()
        );
    }

    @Override
    public TokenType getType() {
        return TokenType.REFRESH;
    }
}
