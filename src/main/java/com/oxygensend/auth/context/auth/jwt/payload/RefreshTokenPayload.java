package com.oxygensend.auth.context.auth.jwt.payload;

import com.oxygensend.auth.domain.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
@ToString
public class RefreshTokenPayload extends TokenPayload {

    private final UUID sessionId;

    public RefreshTokenPayload(UUID sessionId, Date iat, Date exp) {
        super(TokenType.REFRESH, iat, exp);
        this.sessionId = sessionId;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(sessionId.toString())
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .build();
    }
}
