package com.oxygensend.auth.context.jwt.payload;

import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.UserRole;
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
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String userId;
    private final Set<UserRole> roles;


    public AccessTokenPayload(String firstName, String lastName, String email, String userId, Set<UserRole> roles, Date iat, Date exp) {
        super(TokenType.ACCESS, iat, exp);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
        this.roles = roles;
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
                   .add("userId", userId)
                   .add("roles", roles)
                   .build();
    }
}
