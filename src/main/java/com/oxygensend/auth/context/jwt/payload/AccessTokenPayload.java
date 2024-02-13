package com.oxygensend.auth.context.jwt.payload;

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
public class AccessTokenPayload extends TokenPayload {
    private final String firstName;
    private final String lastName;
    private final String email;

    public AccessTokenPayload(String firstName, String lastName, String email, Date iat, Date exp) {
        super(TokenType.ACCESS, iat, exp);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(email)
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .add("firstName", firstName)
                   .add("lastName", lastName)
                   .build();
    }
}
