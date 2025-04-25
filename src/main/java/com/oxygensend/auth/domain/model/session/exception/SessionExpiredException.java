package com.oxygensend.auth.domain.model.session.exception;

import common.domain.model.DomainException;

public class SessionExpiredException extends DomainException {
    public SessionExpiredException() {
        super("Session has expired");
    }

}
