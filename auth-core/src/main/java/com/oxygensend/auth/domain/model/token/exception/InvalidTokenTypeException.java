package com.oxygensend.auth.domain.model.token.exception;

import com.oxygensend.common.domain.model.DomainException;

public class InvalidTokenTypeException extends DomainException {
    public InvalidTokenTypeException() {
        super("Invalid token type");
    }
}
