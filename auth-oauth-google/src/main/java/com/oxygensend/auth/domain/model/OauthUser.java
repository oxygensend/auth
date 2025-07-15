package com.oxygensend.auth.domain.model;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.GoogleId;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;

import java.util.Set;

public class OauthUser extends User {


    public OauthUser(UserId id, Credentials credentials, Set<Role> roles, boolean locked, boolean verified,
                     BusinessId businessId, AccountActivationType accountActivationType, GoogleId googleId) {
        super(id, credentials, roles, locked, verified, businessId, accountActivationType, googleId);
    }

    public static OauthUser registerUser(UserId id,
                                    Set<Role> roles,
                                    Credentials credentials,
                                    BusinessId businessId,
                                    GoogleId googleId) {
        return new OauthUser(id, credentials, roles, false, true, businessId,
                        AccountActivationType.GOOGLE_OAUTH, googleId);
    }
}
