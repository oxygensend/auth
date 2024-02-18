package com.oxygensend.auth.context.auth.jwt.factory;

import com.oxygensend.auth.context.auth.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRole;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
final class AccessTokenPayloadFactory implements TokenPayloadFactory {
    @Override
    public TokenPayload createToken(Date exp, Date iat, User user) {
        return new AccessTokenPayload(
                user.firstName(),
                user.lastName(),
                user.email(),
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
