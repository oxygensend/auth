package com.oxygensend.auth.application.auth;

public class ExpiredRefreshTokenException extends RuntimeException{

    public ExpiredRefreshTokenException() {
        super("Refresh token is expired");
    }

}
