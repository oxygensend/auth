package com.oxygensend.auth.context.jwt.factory;

import com.oxygensend.auth.context.IdentityProvider;
import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRole;
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
                user.firstName(),
                user.lastName(),
                identityProvider.getIdentity(user),
                user.id().toString(),
                user.roles(),
                iat,
                exp);
    }

    @Override
    public TokenPayload createToken(Claims claims) {
        return new AccessTokenPayload(
                (String) claims.get("firstName"),
                (String) claims.get("lastName"),
                claims.getSubject(),
                (String) claims.get("userId"),
                new HashSet<>(((List<UserRole>) claims.get("roles"))),
                claims.getIssuedAt(),
                claims.getExpiration());
    }

    @Override
    public TokenType getType() {
        return TokenType.ACCESS;
    }
}
