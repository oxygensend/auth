package com.oxygensend.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {

    Session save(Session session);

    void deleteById(UUID id);

    Optional<Session> findById(UUID id);
}
