package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.auth.domain.model.identity.Role;

import java.util.List;

import common.domain.model.DomainException;

public class UnexpectedRoleException extends DomainException {
    public UnexpectedRoleException(Role role) {
        super("Provided unexpected role %s".formatted(role));
    }

    public UnexpectedRoleException(List<Role> roles) {
        super("Provided unexpected roles %s".formatted(roles));
    }
}
