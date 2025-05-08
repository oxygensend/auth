package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.PasswordResetTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.UserId;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
final class PasswordResetTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createPayload(Date exp, Date iat, TokenSubject subject) {
        if (!(subject instanceof PasswordResetTokenSubject)) {
            throw new IllegalArgumentException("Invalid token subject type: " + subject.getClass());
        }
        return new PasswordResetTokenPayload(subject.userId(), TokenType.PASSWORD_RESET, iat, exp);
    }

    @Override
    public TokenPayload createPayload(Claims claims) {
        return new PasswordResetTokenPayload(new UserId(claims.getSubject()), TokenType.PASSWORD_RESET, claims.getIssuedAt(), claims.getExpiration());
    }

    @Override
    public TokenType getType() {
        return TokenType.PASSWORD_RESET;
    }
}
