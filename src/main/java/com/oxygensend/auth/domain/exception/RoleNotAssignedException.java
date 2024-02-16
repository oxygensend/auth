package com.oxygensend.auth.domain.exception;

import com.oxygensend.commons_jdk.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotAssignedException extends ApiException {
    public RoleNotAssignedException() {
        super("Role not assigned to user.");
    }
}
