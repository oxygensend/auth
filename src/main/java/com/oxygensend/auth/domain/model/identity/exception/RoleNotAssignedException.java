package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import common.domain.model.DomainException;

public class RoleNotAssignedException extends DomainException {
    public RoleNotAssignedException() {
        super("Role not assigned to user.");
    }
}
