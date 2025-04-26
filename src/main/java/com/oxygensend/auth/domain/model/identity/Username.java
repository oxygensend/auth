package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.application.settings.LoginDto;


import common.AssertionConcern;

public record Username(String value) {

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
