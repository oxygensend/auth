package com.oxygensend.auth.application;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException withId(UserId id) {
        return new UserNotFoundException("User with id " + id + " not found");
    }
}
