package com.oxygensend.auth.port.adapter.in.rest.exception;

import com.oxygensend.auth.application.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.application.identity.exception.UserNotFoundException;
import com.oxygensend.auth.domain.model.identity.exception.BlockedUserException;
import com.oxygensend.auth.domain.model.identity.exception.ExpiredCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import common.domain.model.DomainException;
import common.domain.model.DomainModelConflictException;
import common.domain.model.DomainModelValidationException;

@ControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new ExceptionResponse(HttpStatus.BAD_REQUEST, error, ex));
    }

    @ExceptionHandler({DomainModelConflictException.class})
    public ResponseEntity<Object> handleDomainModelConflict(DomainModelConflictException ex) {
        logger.info("Throwing an exception: {}", ex);
        return buildResponseEntity(new ExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler({DomainModelValidationException.class, UnexpectedRoleException.class})
    public ResponseEntity<Object> handleValidationExceptions(RuntimeException ex) {
        logger.info("Throwing an exception: {}", ex);
        return buildResponseEntity(new ExceptionResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler({BlockedUserException.class, ExpiredCredentialsException.class})
    public ResponseEntity<Object> handleDomainModelForbidden(DomainException ex) {
        logger.info("Throwing an exception: {}", ex);
        return buildResponseEntity(new ExceptionResponse(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(DomainException ex) {
        logger.info("Throwing an exception: {}", ex);
        return buildResponseEntity(new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        ExceptionResponse apiException = new ExceptionResponse(HttpStatus.BAD_REQUEST, "Validation error", ex);
        apiException.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiException.addValidationException(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiException);
    }


    private ResponseEntity<Object> buildResponseEntity(ExceptionResponse exceptionResponse) {
        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getStatus());
    }

}
