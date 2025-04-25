package com.oxygensend.auth.domain.model.identity;

import common.AssertionConcern;
import java.util.UUID;

public record UserId(UUID value) {

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
