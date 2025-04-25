package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.application.auth.LoginDto;


import common.AssertionConcern;

public record UserName(String value) {

    public UserName {
        AssertionConcern.assertArgumentNotEmpty(value, "Username cannot be empty");
        AssertionConcern.assertArgumentLength(value, 4, 64, "Username length invalid");
    }

    public UserName(EmailAddress email) {
        this(email.address());
    }

    public UserName(LoginDto login) {
        this(login.value());
    }

    @Override
    public String toString() {
       return value;
    }
}
