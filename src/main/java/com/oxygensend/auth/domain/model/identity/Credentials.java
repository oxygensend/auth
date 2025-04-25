package com.oxygensend.auth.domain.model.identity;

import common.AssertionConcern;

public record Credentials(EmailAddress email,
                          UserName userName,
                          Password password,
                          boolean expired) {

    public Credentials(EmailAddress email, UserName userName, Password password) {
        this(email, userName, password, false);
    }

    public Credentials {
        AssertionConcern.assertArgumentNotNull(email, "Email cannot be null");
        AssertionConcern.assertArgumentNotNull(password, "Password cannot be null");
        AssertionConcern.assertArgumentNotNull(userName, "Expired cannot be null");
    }

    public Credentials passwordChanged(Password password){
        return new Credentials(email, userName, password, expired);
    }

    public boolean passwordMatches(String rawPassword, PasswordService passwordService) {
        return password.matches(rawPassword, passwordService);
    }

    public boolean isNonExpired(){
        return !expired;
    }
    @Override
    public UserName userName() {
        return userName != null ? userName : new UserName(email);
    }
}
