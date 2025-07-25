package com.oxygensend.auth.domain.model.identity;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.oxygensend.common.AssertionConcern;

public record Credentials(EmailAddress email,
                          Username username,
                          Password password,
                          boolean expired) {

    @JsonCreator
    public Credentials(EmailAddress email, Username userName, Password password) {
        this(email, userName, password, false);
    }

    public Credentials(EmailAddress email, Username username) {
        this(email, username, null);
    }

    public Credentials {
        AssertionConcern.assertArgumentNotNull(email, "Email cannot be null");
        AssertionConcern.assertArgumentNotNull(username, "Username cannot be null");
    }

    public Credentials passwordChanged(Password password) {
        return new Credentials(email, username, password, expired);
    }

    public boolean passwordMatches(String rawPassword, PasswordService passwordService) {
        return password.matches(rawPassword, passwordService);
    }

    public boolean isNonExpired() {
        return !expired;
    }

    public Username username() {
        return username;
    }
}
