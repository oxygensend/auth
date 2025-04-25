package com.oxygensend.auth.domain.model.session;

import java.util.UUID;

import common.AssertionConcern;

public record SessionId(UUID value) {

    public SessionId {
        AssertionConcern.assertArgumentNotNull(value, "SessionId value cannot be null");
    }
}