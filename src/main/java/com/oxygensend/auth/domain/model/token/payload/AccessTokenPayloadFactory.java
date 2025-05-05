package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
final class AccessTokenPayloadFactory implements TokenPayloadFactory {

    // No fields requiring constructor injection


    @Override
    public TokenPayload createPayload(Date exp, Date iat, TokenSubject subject) {
        if (!(subject instanceof AccessTokenSubject)) {
            throw new IllegalArgumentException("Invalid token subject type: " + subject.getClass());
        }
        return new AccessTokenPayload(
            ((AccessTokenSubject) subject).username(),
                subject.userId(),
                ((AccessTokenSubject) subject).roles(),
                iat,
                exp,
                ((AccessTokenSubject) subject).verified(),
                ((AccessTokenSubject) subject).businessId(),
                ((AccessTokenSubject) subject).email()
        );
    }

    @Override
    public TokenPayload createPayload(Claims claims) {
        return new AccessTokenPayload(
            new Username(claims.get("username", String.class)),
            new UserId(claims.get("userId", String.class)),
            new HashSet<>(((claims.get("roles") instanceof List) ? ((List<?>) claims.get("roles")) : List.of()).stream()
                .map(role -> new Role(role.toString()))
                .toList()),
            claims.getIssuedAt(),
            claims.getExpiration(),
            claims.get("verified", Boolean.class),
            new BusinessId(claims.get("businessId", String.class)),
            new EmailAddress(claims.getSubject())
        );
    }

    @Override
    public TokenType getType() {
        return TokenType.ACCESS;
    }
}
