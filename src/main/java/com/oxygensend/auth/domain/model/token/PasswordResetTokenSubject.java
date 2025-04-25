package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.UserId;

public class PasswordResetTokenSubject extends TokenSubject {


    public PasswordResetTokenSubject(UserId userId) {
        super(userId);
    }

        @Override
   public TokenType tokenType() {
        return TokenType.PASSWORD_RESET;
    }
}
