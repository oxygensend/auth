package com.oxygensend.auth.domain.model.token;

import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenException extends ApiException {
    public TokenException(String message) {
        super(message);
    }
}
