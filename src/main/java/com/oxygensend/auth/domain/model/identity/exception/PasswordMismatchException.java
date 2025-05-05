package com.oxygensend.auth.domain.model.identity.exception;



import common.domain.model.DomainException;

public class PasswordMismatchException extends DomainException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
