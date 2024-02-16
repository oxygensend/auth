package com.oxygensend.auth.helper;

import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRole;
import java.util.List;
import java.util.UUID;
import org.assertj.core.util.Sets;

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
                   .roles(Sets.newHashSet(List.of(UserRole.ROLE_USER)))
                   .email("test@test.com")
                   .build();
    }
}
