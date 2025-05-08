package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.auth.domain.model.identity.UserId;

import common.domain.model.DomainException;

public class BlockedUserException extends DomainException {
    public BlockedUserException(UserId userId) {
        super("User with id %s is blocked".formatted(userId));
    }
}
