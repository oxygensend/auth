package com.oxygensend.auth.context.auth.jwt.payload;

import com.oxygensend.auth.domain.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
@ToString
public class PasswordResetTokenPayload extends TokenPayload {
    private final String userId;

    public PasswordResetTokenPayload(String userId, TokenType type, Date iat, Date exp) {
        super(type, iat, exp);
        this.userId = userId;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(userId)
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .build();
    }
}