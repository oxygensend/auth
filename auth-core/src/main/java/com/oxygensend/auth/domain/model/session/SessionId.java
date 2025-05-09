package com.oxygensend.auth.domain.model.session;

import java.util.UUID;

import com.oxygensend.common.AssertionConcern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SessionId(UUID value) {

    @JsonCreator
    public SessionId {
        AssertionConcern.assertArgumentNotNull(value, "SessionId value cannot be null");
    }

    @JsonValue
    @Override
    public UUID value() {
        return value;
    }
}
