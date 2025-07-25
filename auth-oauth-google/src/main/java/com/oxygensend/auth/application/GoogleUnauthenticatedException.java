package com.oxygensend.auth.application;

public class GoogleUnauthenticatedException extends RuntimeException {
    public GoogleUnauthenticatedException(Exception ex){
        super("Failed to authenticate in google", ex);

    }
}
