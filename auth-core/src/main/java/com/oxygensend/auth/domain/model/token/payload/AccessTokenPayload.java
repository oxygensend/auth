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
import java.util.Objects;

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

    public Username username() {
        return username;
    }

    public UserId userId() {
        return userId;
    }

    public Set<Role> roles() {
        return roles;
    }


    public boolean verified() {
        return verified;
    }


    public BusinessId businessId() {
        return businessId;
    }

    public EmailAddress email() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessTokenPayload that = (AccessTokenPayload) o;
        return verified == that.verified && 
               Objects.equals(username, that.username) && 
               Objects.equals(userId, that.userId) && 
               Objects.equals(roles, that.roles) && 
               Objects.equals(businessId, that.businessId) && 
               Objects.equals(email, that.email);
    }


    @Override
    public int hashCode() {
        return Objects.hash(username, userId, roles, verified, businessId, email);
    }

    @Override
    public String toString() {
        return "AccessTokenPayload{" +
               "username=" + username +
               ", userId=" + userId +
               ", roles=" + roles +
               ", verified=" + verified +
               ", businessId=" + businessId +
               ", email=" + email +
               '}';
    }

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
