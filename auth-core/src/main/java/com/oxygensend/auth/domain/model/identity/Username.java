package com.oxygensend.auth.domain.model.identity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.oxygensend.auth.application.settings.LoginDto;

import com.oxygensend.common.AssertionConcern;

public record Username(@JsonValue String value) {

    public Username {
        AssertionConcern.assertArgumentNotEmpty(value, "Username cannot be empty");
        AssertionConcern.assertArgumentLength(value, 4, 64, "Username length invalid");
    }

    public Username(EmailAddress email) {
        this(email.address());
    }

    public Username(LoginDto login) {
        this(login.value());
    }

    @Override
    public String toString() {
        return value;
    }
}
