package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.EmailVerificationTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.UserId;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
final class EmailVerificationTokenPayloadFactory implements TokenPayloadFactory {

    @Override
    public TokenPayload createPayload(Date exp, Date iat, TokenSubject tokenSubject) {
        if (!(tokenSubject instanceof EmailVerificationTokenSubject)) {
            throw new IllegalArgumentException("Invalid token subject type: " + tokenSubject.getClass());
        }
        return new EmailVerificationTokenPayload(TokenType.EMAIL_VERIFICATION, iat, exp, tokenSubject.userId());
    }

    @Override
    public TokenPayload createPayload(Claims claims) {
        return new EmailVerificationTokenPayload(TokenType.EMAIL_VERIFICATION, claims.getIssuedAt(), claims.getExpiration(), new UserId(claims.getSubject()));
    }

    @Override
    public TokenType getType() {
        return TokenType.EMAIL_VERIFICATION;
    }
}
