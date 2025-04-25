package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;

import java.util.Set;

public class AccessTokenSubject extends TokenSubject {
   private final BusinessId businessId;
    private final Set<Role> roles;
    private final boolean verified;
    private final UserName userName;
    private final EmailAddress email;

    public AccessTokenSubject(UserId userId, BusinessId businessId, Set<Role> roles, boolean verified,
                              UserName userName, EmailAddress email) {
        super(userId);
        this.businessId = businessId;
        this.roles = roles;
        this.verified = verified;
        this.userName = userName;
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

    public UserName userName() {
        return userName;
    }

    public EmailAddress email() {
        return email;
    }
    @Override
    public TokenType tokenType() {
       return TokenType.ACCESS;
    }
}
