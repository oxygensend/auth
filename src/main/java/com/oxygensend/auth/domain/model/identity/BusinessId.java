package com.oxygensend.auth.domain.model.identity;

import static common.AssertionConcern.assertArgumentNotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record BusinessId(@JsonValue String value) {
    @JsonCreator
    public BusinessId {
        assertArgumentNotEmpty(value, "BusinessId value cannot be empty");
    }

    @Override
    public String toString() {
        return value;
    }
}
