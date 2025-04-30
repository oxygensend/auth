package com.oxygensend.auth.helper;

import com.oxygensend.auth.domain.model.session.SessionId;

public class SessionMother {

    public static SessionId getRandomSessionId() {
        return new SessionId(java.util.UUID.randomUUID());
    }
}
