package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainModelConflictException;

public class RoleAlreadyAssignedException extends DomainModelConflictException {
    public RoleAlreadyAssignedException() {
        super("User role already exists.");
    }
}
