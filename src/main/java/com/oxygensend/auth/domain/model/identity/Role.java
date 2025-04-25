package com.oxygensend.auth.domain.model.identity;

import common.AssertionConcern;

public record Role(String value) {

    public Role {
        AssertionConcern.assertArgumentNotEmpty(value, "Role value cannot be empty");
    }

    @Override
    public String toString() {
       return value;
    }
}