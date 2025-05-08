package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.UserId;

public class RefreshTokenSubject extends TokenSubject{

    public RefreshTokenSubject(UserId userId) {
        super(userId);
    }

    @Override
    public TokenType tokenType() {
       return TokenType.REFRESH;
    }
}
