package com.oxygensend.auth.domain.model.token.exception;

import common.domain.model.DomainException;

public class TokenException extends DomainException {
    public TokenException(String message) {
        super(message);
    }
}
