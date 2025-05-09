package com.oxygensend.auth.domain.model.token;

import com.oxygensend.auth.domain.model.identity.UserId;

public class EmailVerificationTokenSubject extends TokenSubject {

    public EmailVerificationTokenSubject(UserId userId) {
        super(userId);
    }

    @Override
    public TokenType tokenType() {
       return TokenType.EMAIL_VERIFICATION;
    }
}
