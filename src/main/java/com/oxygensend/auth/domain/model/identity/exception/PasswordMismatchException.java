package com.oxygensend.auth.domain.model.identity.exception;


import common.domain.model.DomainModelValidationException;

public class PasswordMismatchException extends DomainModelValidationException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
