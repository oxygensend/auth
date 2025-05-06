package com.oxygensend.auth.domain.model.identity.exception;


import common.domain.model.DomainModelValidationException;

public class RoleNotAssignedException extends DomainModelValidationException {
    public RoleNotAssignedException() {
        super("Role not assigned to user.");
    }
}
