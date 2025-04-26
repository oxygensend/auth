package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;

import java.util.Set;

public class AccessTokenSubject extends TokenSubject {
   private final BusinessId businessId;
    private final Set<Role> roles;
    private final boolean verified;
    private final Username username;
    private final EmailAddress email;

    public AccessTokenSubject(UserId userId, BusinessId businessId, Set<Role> roles, boolean verified,
                              Username username, EmailAddress email) {
        super(userId);
        this.businessId = businessId;
        this.roles = roles;
        this.verified = verified;
        this.username = username;
        this.email = email;
    }

    public BusinessId businessId() {
        return businessId;
    }
    public Set<Role> roles() {
        return roles;
    }
    public boolean verified() {
        return verified;
    }

    public Username username() {
        return username;
    }

    public EmailAddress email() {
        return email;
    }
    @Override
    public TokenType tokenType() {
       return TokenType.ACCESS;
    }
}
