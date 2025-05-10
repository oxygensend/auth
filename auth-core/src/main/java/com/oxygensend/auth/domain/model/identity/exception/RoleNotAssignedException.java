package com.oxygensend.auth.domain.model.identity.exception;


import com.oxygensend.common.domain.model.DomainModelValidationException;

public class RoleNotAssignedException extends DomainModelValidationException {
    public RoleNotAssignedException() {
        super("Role not assigned to user.");
    }
}
