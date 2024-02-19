package com.oxygensend.auth.context.auth.jwt.factory;

import com.oxygensend.auth.context.auth.jwt.payload.PasswordResetTokenPayload;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
final class PasswordResetTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new PasswordResetTokenPayload(user.id().toString(), TokenType.PASSWORD_RESET, iat, exp);
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new PasswordResetTokenPayload(claims.getSubject(), TokenType.PASSWORD_RESET, claims.getIssuedAt(), claims.getExpiration());
    }

    @Override
    public TokenType getType() {
        return TokenType.PASSWORD_RESET;
    }
}
