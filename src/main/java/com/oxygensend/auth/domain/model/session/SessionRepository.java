package com.oxygensend.auth.domain.model.session;

import com.oxygensend.auth.domain.model.identity.UserId;

import java.util.Optional;

public interface SessionRepository {
    SessionId nextIdentity();

    Session save(Session session);

    void removeByUserId(UserId id);

    Optional<Session> sessionOfUserId(UserId userId);
}
