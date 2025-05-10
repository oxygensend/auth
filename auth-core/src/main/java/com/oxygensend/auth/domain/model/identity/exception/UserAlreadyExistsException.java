package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.common.domain.model.DomainModelConflictException;

public class UserAlreadyExistsException extends DomainModelConflictException {
    public UserAlreadyExistsException() {
        super("User with this username or email already exists.");
    }


}
