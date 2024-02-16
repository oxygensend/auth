package com.oxygensend.auth.helper;

import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRole;
import java.util.Set;
import java.util.UUID;

public final class UserMother {
    private UserMother() {
    }

    public static User getRandom() {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Tester")
                .lastName("Tester")
                .password("test")
                .enabled(true)
                .locked(false)
                .roles(Set.of(UserRole.ROLE_USER))
                .email("test@test.com")
                .build();
    }
}
