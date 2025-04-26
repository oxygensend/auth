package com.oxygensend.auth.domain.model.identity.exception;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import common.domain.model.DomainException;


public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException withId(UserId id) {
        return new UserNotFoundException("User with id " + id + " not found");
    }
}
