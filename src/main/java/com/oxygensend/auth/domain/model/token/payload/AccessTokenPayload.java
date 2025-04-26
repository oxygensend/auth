package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
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
    private final Username username;
    private final UserId userId;
    private final Set<Role> roles;
    private final boolean verified;
    private final BusinessId businessId;
    private final EmailAddress email;


    public AccessTokenPayload(Username username,
                              UserId userId,
                              Set<Role> roles,
                              Date iat,
                              Date exp,
                              boolean verified,
                              BusinessId businessId,
                              EmailAddress email) {
        super(TokenType.ACCESS, iat, exp);
        this.username = username;
        this.userId = userId;
        this.roles = roles;
        this.verified = verified;
        this.businessId = businessId;
        this.email = email;
    }

    @Override
    public Claims toClaims() {
        return Jwts.claims()
                   .subject(email.toString())
                   .issuedAt(iat)
                   .expiration(exp)
                   .add("type", type)
                   .add("userId", userId.value())
                   .add("businessId", businessId.value())
                   .add("roles", roles.stream().map(Role::value).toList())
                   .add("verified", verified)
                   .add("username", username.value())
                   .build();
    }
}
