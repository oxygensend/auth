package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.domain.model.identity.Credentials;

public class LoginProvider {
    private final LoginType loginType;

    public LoginProvider(LoginType loginType) {
        this.loginType = loginType;
    }

    public LoginDto get(Credentials credentials) {
        return switch (loginType) {
            case USERNAME -> new LoginDto(credentials.userName().value(), loginType);
            case EMAIL -> new LoginDto(credentials.email().address(), loginType);
        };
    }

    public LoginDto get(String login) {
        return new LoginDto(login, loginType);
    }

}
