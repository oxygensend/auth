package com.oxygensend.auth.domain.exception;

import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionExpiredException extends ApiException {
    public SessionExpiredException() {
        super("Session has expired");
    }

    public SessionExpiredException(String message) {
        super(message);
    }
}
