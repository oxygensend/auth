package com.oxygensend.auth.helper;

import com.oxygensend.auth.domain.User;
import java.util.List;
import java.util.UUID;
import org.assertj.core.util.Sets;

public final class UserMother {
    private UserMother() {
    }

    public static User getRandom() {
        return User.builder()
                   .id(UUID.randomUUID())
                   .password("test")
                   .verified(true)
                   .locked(false)
                   .roles(Sets.newHashSet(List.of("ROLE_USER")))
                   .email("test@test.com")
                   .build();
    }
}
