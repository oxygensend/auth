package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.application.settings.LoginDto;

import java.util.Objects;

import common.AssertionConcern;

public final class EmailAddress {
    public static final String EMAIL_PATTERN =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final String address;

    public EmailAddress(String address) {
        AssertionConcern.assertArgumentNotEmpty(address, "Email address cannot be empty");
        AssertionConcern.assertArgumentMatches(address, EMAIL_PATTERN, "Invalid email address format: " + address);

        this.address = address;
    }

    public EmailAddress(LoginDto login) {
        this(login.value());
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailAddress that = (EmailAddress) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }

    @Override
    public String toString() {
       return address;
    }
}
