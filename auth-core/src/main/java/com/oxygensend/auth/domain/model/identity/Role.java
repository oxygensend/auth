package com.oxygensend.auth.domain.model.identity;

import com.fasterxml.jackson.annotation.JsonValue;

import com.oxygensend.common.AssertionConcern;

public record Role(@JsonValue String value) {

    public Role {
        AssertionConcern.assertArgumentNotEmpty(value, "Role value cannot be empty");
    }

    @Override
    public String toString() {
        return value;
    }
}