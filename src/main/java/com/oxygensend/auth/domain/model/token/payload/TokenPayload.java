package com.oxygensend.auth.domain.model.token.payload;


import com.oxygensend.auth.domain.model.token.TokenType;

import java.util.Date;
import java.util.Objects;

abstract public class TokenPayload implements ClaimsPayload {
    protected final TokenType type;
    protected final Date iat;
    protected final Date exp;

    public TokenPayload(TokenType type, Date iat, Date exp) {
        this.type = type;
        this.iat = iat;
        this.exp = exp;
    }

    public TokenType type() {
        return type;
    }

    public Date iat() {
        return iat;
    }

    public Date exp() {
        return exp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenPayload that = (TokenPayload) o;
        return type == that.type && 
               Objects.equals(iat, that.iat) && 
               Objects.equals(exp, that.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, iat, exp);
    }

    @Override
    public String toString() {
        return "TokenPayload{" +
               "type=" + type +
               ", iat=" + iat +
               ", exp=" + exp +
               '}';
    }
}
