package com.oxygensend.auth.domain.model.identity;

import static common.AssertionConcern.assertArgumentNotEmpty;


public record BusinessId(String value) {
    public BusinessId {
        assertArgumentNotEmpty(value, "BusinessId value cannot be empty");
    }

    @Override
    public String toString() {
        return value;
    }
}