package com.oxygensend.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(UUID id);

    boolean existsById(UUID uuid);

    void deleteById(UUID uuid);
}
