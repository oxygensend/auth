package com.oxygensend.auth.application;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(Exception ex){
        super("Failed to authenticate in google", ex);

    }
}
