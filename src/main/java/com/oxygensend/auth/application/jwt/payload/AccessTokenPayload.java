package com.oxygensend.auth.application.jwt.payload;

import com.oxygensend.auth.domain.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
@ToString
public class AccessTokenPayload extends TokenPayload {
    private final String identity;
    private final String userId;
    private final Set<String> roles;
    private final boolean verified;
    private final String businessId;


    public AccessTokenPayload(String identity, String userId, Set<String> roles, Date iat, Date exp, boolean verified, String businessId) {
        super(TokenType.ACCESS, iat, exp);
        this.identity = identity;
        this.userId = userId;
        this.roles = roles;
        this.verified = verified;
        this.businessId = businessId;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(identity)
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .add("id", userId)
                   .add("businessId", businessId)
                   .add("roles", roles)
                   .add("verified", verified)
                   .build();
    }
}
