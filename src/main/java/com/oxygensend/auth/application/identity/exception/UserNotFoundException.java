package com.oxygensend.auth.application.identity.exception;

import com.oxygensend.auth.domain.model.identity.UserId;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException withId(UserId id) {
        return new UserNotFoundException("User with id " + id + " not found");
    }
}
