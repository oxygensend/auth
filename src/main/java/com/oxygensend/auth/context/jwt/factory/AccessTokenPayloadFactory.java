package com.oxygensend.auth.context.jwt.factory;

import com.oxygensend.auth.context.IdentityProvider;
import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
final class AccessTokenPayloadFactory implements TokenPayloadFactory {

    private final IdentityProvider identityProvider;

    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new AccessTokenPayload(
                identityProvider.getIdentity(user),
                user.id().toString(),
                user.roles(),
                iat,
                exp,
                user.verified());
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new AccessTokenPayload(
                claims.getSubject(),
                claims.get("userId", String.class),
                new HashSet<>(((List<String>) claims.get("roles"))),
                claims.getIssuedAt(),
                claims.getExpiration(),
                claims.get("verified", Boolean.class));
    }

    @Override
    public TokenType getType() {
        return TokenType.ACCESS;
    }
}
