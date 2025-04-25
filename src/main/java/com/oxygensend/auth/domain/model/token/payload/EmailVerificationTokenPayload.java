package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.UserId;
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
public class EmailVerificationTokenPayload extends TokenPayload {

    private final UserId userId;

    public EmailVerificationTokenPayload(TokenType type, Date iat, Date exp, UserId userId) {
        super(type, iat, exp);
        this.userId = userId;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(userId.toString())
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .build();
    }
}
