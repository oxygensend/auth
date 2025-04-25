package com.oxygensend.auth.domain.model.identity.exception;

import common.domain.model.DomainException;

public class BadCredentialsException extends DomainException {
    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException(){
        super("Bad credentials");
    }
}
