package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import common.domain.model.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException() {
        super("User with this email already exists.");
    }

}
