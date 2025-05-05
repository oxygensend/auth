package com.oxygensend.auth.domain.model.identity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

import common.AssertionConcern;

public final class Password {
    private final String hashedValue;

    private Password(String hashedValue) {
        AssertionConcern.assertArgumentNotEmpty(hashedValue, "Hashed password cannot be null or empty");
        this.hashedValue = hashedValue;
    }

    public static Password fromPlaintext(String plaintext, PasswordService passwordService) {
        AssertionConcern.assertArgumentLength(plaintext, 4, 64, "Password must be between 4 and 64 characters");
        return new Password(passwordService.encode(plaintext));
    }

    @JsonCreator
    public static Password fromHashed(String hashedValue) {
        return new Password(hashedValue);
    }

    public boolean matches(String rawPassword, PasswordService passwordService) {
        return passwordService.matches(rawPassword, this.hashedValue);
    }

    @JsonValue
    public String hashedValue() {
        return hashedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Password password = (Password) o;
        return Objects.equals(hashedValue, password.hashedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hashedValue);
    }


    @Override
    public String toString() {
        return hashedValue;
    }
}
