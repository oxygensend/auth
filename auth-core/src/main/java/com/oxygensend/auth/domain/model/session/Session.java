package com.oxygensend.auth.domain.model.session;

import com.oxygensend.auth.domain.model.identity.UserId;

import com.oxygensend.common.AssertionConcern;

public record Session(SessionId id,
                      UserId userId) {

    public Session {
        AssertionConcern.assertArgumentNotNull(id, "Session id cannot be null");
        AssertionConcern.assertArgumentNotNull(userId, "Session userId cannot be null");
    }
}
