package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainException;
import common.domain.model.DomainModelConflictException;

public class UserAlreadyExistsException extends DomainModelConflictException {
    private UserAlreadyExistsException(String message) {
        super(message);
    }


    public static UserAlreadyExistsException withUsername() {
        return new UserAlreadyExistsException("User with this username or email already exists.");
    }

    public static UserAlreadyExistsException withEmail() {
        return new UserAlreadyExistsException("User with this email already exists.");
    }
}
