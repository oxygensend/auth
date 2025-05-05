package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Objects;

public class PasswordResetTokenPayload extends TokenPayload {
    private final UserId userId;

    public PasswordResetTokenPayload(UserId userId, TokenType type, Date iat, Date exp) {
        super(type, iat, exp);
        this.userId = userId;
    }

    public UserId userId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetTokenPayload that = (PasswordResetTokenPayload) o;
        return Objects.equals(userId, that.userId) && 
               type == that.type && 
               Objects.equals(iat, that.iat) && 
               Objects.equals(exp, that.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, type, iat, exp);
    }

    @Override
    public String toString() {
        return "PasswordResetTokenPayload{" +
               "userId=" + userId +
               ", type=" + type +
               ", iat=" + iat +
               ", exp=" + exp +
               '}';
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
