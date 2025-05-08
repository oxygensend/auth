package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.UserId;

public abstract class TokenSubject {
    private final UserId userId;

    protected TokenSubject(UserId userId) {
        this.userId = userId;
    }

    public UserId userId() {
        return userId;
    }

    abstract public TokenType tokenType();
}
