package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainException;

public class UserAlreadyExistsException extends DomainException {
    private UserAlreadyExistsException(String message) {
        super(message);
    }


    public static UserAlreadyExistsException withUsername() {
        return new UserAlreadyExistsException("User with this username already exists.");
    }

    public static UserAlreadyExistsException withEmail() {
        return new UserAlreadyExistsException("User with this email already exists.");
    }
}
