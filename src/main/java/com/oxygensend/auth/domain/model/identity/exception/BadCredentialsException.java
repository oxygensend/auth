package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainModelValidationException;

public class BadCredentialsException extends DomainModelValidationException {

    public BadCredentialsException() {
        super("Bad credentials");
    }
}
