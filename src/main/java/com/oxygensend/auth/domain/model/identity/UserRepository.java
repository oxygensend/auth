package com.oxygensend.auth.domain.model.identity;

import java.util.Optional;

public interface UserRepository {

    UserId nextIdentity();

    Optional<User> findByEmail(EmailAddress email);

    User save(User user);

    Optional<User> findById(UserId id);

    boolean existsById(UserId uuid);

    boolean existsByEmail(EmailAddress email);

    boolean existsByUsername(Username username);

    void deleteById(UserId uuid);

    Optional<User> findByUsername(Username username);
}
