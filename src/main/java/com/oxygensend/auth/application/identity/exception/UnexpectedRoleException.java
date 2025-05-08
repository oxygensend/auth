package com.oxygensend.auth.application.identity.exception;

import com.oxygensend.auth.domain.model.identity.Role;

import java.util.List;

public class UnexpectedRoleException extends RuntimeException {
    public UnexpectedRoleException(Role role) {
        super("Provided unexpected role %s".formatted(role));
    }

    public UnexpectedRoleException(List<Role> roles) {
        super("Provided unexpected roles %s".formatted(roles));
    }
}
