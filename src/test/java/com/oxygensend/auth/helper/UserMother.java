package com.oxygensend.auth.helper;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;

import java.util.Set;
import java.util.UUID;

public final class UserMother {
    private UserMother() {
    }

    public static User getRandom() {
        var credentials = new Credentials(
            new EmailAddress("test@test.pl"),
            new Username("username"),
            Password.fromHashed("hashed-password"));
        return new User(
            new UserId(UUID.randomUUID()),
            credentials,
            Set.of(new Role("ROLE_ADMIN")),
            false,
            true,
            new BusinessId(UUID.randomUUID().toString()),
            AccountActivationType.NONE
        );
    }

    public static UserId getRandomUserId() {
        return new UserId(UUID.randomUUID());
    }
}
