package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainException;

public class RoleAlreadyExistsException extends DomainException {
    public RoleAlreadyExistsException() {
        super("User role already exists.");
    }
}
