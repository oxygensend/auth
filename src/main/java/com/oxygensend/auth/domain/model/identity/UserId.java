package com.oxygensend.auth.domain.model.identity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

import common.AssertionConcern;

public record UserId(@JsonValue UUID value) {

    public UserId {
        AssertionConcern.assertArgumentNotNull(value, "UserId value cannot be null");
    }

    public UserId(String value) {
        this(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
