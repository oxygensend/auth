package com.oxygensend.auth.port.in.rest;

import com.oxygensend.auth.application.GoogleUnauthenticatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice(basePackages = "com.oxygensend.auth.port.in.rest")
public class GoogleOAuthExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthExceptionHandler.class);

    @ExceptionHandler(GoogleUnauthenticatedException.class)
    public ResponseEntity<Object> handleGoogleUnauthenticated(GoogleUnauthenticatedException ex) {
        logger.info("Google OAuth authentication failed: {}", ex.getMessage(), ex);
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", "UNAUTHORIZED",
                        "message", ex.getMessage(),
                        "error", "Google OAuth authentication failed"
                ));
    }
}