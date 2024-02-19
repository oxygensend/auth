package com.oxygensend.auth.context.auth.jwt.factory;

import com.oxygensend.auth.context.auth.jwt.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
final class EmailVerificationTokenPayloadFactory implements TokenPayloadFactory {

    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new EmailVerificationTokenPayload(TokenType.EMAIL_VERIFICATION, iat, exp, user.id().toString());
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new EmailVerificationTokenPayload(TokenType.EMAIL_VERIFICATION, claims.getIssuedAt(), claims.getExpiration(), claims.getSubject());
    }

    @Override
    public TokenType getType() {
        return TokenType.EMAIL_VERIFICATION;
    }
}
