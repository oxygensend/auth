package com.oxygensend.auth.domain.model.identity.exception;



import common.domain.model.DomainException;

public class RoleNotAssignedException extends DomainException {
    public RoleNotAssignedException() {
        super("Role not assigned to user.");
    }
}
