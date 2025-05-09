package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.auth.domain.model.identity.UserId;

import com.oxygensend.common.domain.model.DomainException;

public class ExpiredCredentialsException  extends DomainException {
    public ExpiredCredentialsException(UserId userId) {
        super("User with id %s has expired credentials".formatted(userId));
    }
}
