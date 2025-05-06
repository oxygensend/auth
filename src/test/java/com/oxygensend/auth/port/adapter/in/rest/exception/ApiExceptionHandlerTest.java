package com.oxygensend.auth.port.adapter.in.rest.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.application.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.application.identity.exception.UserNotFoundException;
import com.oxygensend.auth.application.token.InvalidTokenException;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.exception.BadCredentialsException;
import com.oxygensend.auth.domain.model.identity.exception.BlockedUserException;
import com.oxygensend.auth.domain.model.identity.exception.ExpiredCredentialsException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import common.domain.model.DomainException;
import common.domain.model.DomainModelConflictException;
import common.domain.model.DomainModelValidationException;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    private static final UserId userId = new UserId(UUID.randomUUID());
    @Mock
    private WebRequest webRequest;
    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;
    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private BindingResult bindingResult;
    @InjectMocks
    private ApiExceptionHandler exceptionHandler;

    private static Stream<Arguments> provideValidationExceptions() {
        return Stream.of(
            Arguments.of(new DomainModelValidationException("Validation failed"), "Validation failed"),
            Arguments.of(new UnexpectedRoleException(new Role("INVALID_ROLE")), "Provided unexpected role INVALID_ROLE")
        );
    }

    private static Stream<Arguments> provideForbiddenExceptions() {
        return Stream.of(
            Arguments.of(new BlockedUserException(userId), "User with id %s is blocked".formatted(userId)),
            Arguments.of(new ExpiredCredentialsException(userId),
                         "User with id %s has expired credentials".formatted(userId))
        );
    }

    private static Stream<Arguments> provideUnauthenticatedExceptions() {
        return Stream.of(
            Arguments.of(new BadCredentialsException(), "Bad credentials"),
            Arguments.of(new InvalidTokenException(), "Invalid token")
        );
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        // When
        ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(
            httpMessageNotReadableException, headers, status, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Malformed JSON request");
    }

    @Test
    void shouldHandleDomainModelConflict() {
        // Given
        DomainModelConflictException exception = new UserAlreadyExistsException();

        // When
        ResponseEntity<Object> response = exceptionHandler.handleDomainModelConflict(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).isEqualTo("User with this username or email already exists.");
    }

    @ParameterizedTest
    @MethodSource("provideValidationExceptions")
    void shouldHandleValidationExceptions(RuntimeException exception, String expectedMessage) {
        // When
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).contains(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("provideForbiddenExceptions")
    void shouldHandleDomainModelForbidden(DomainException exception, String expectedMessage) {
        // When
        ResponseEntity<Object> response = exceptionHandler.handleDomainModelForbidden(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldHandleUserNotFoundException() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        UserNotFoundException exception = UserNotFoundException.withId(userId);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleUserNotFoundException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).contains(userId.value().toString());
    }

    @ParameterizedTest
    @MethodSource("provideUnauthenticatedExceptions")
    void shouldHandleUnauthenticated(RuntimeException exception, String expectedMessage) {
        // When
        ResponseEntity<Object> response = exceptionHandler.handleUnauthenticated(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        FieldError fieldError = new FieldError(
            "authenticationRequest", "username", null, false, null, null, "Username is required");
        ObjectError globalError = new ObjectError(
            "authenticationRequest", "Invalid request");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

        // When
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
            methodArgumentNotValidException, headers, status, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ExceptionResponse.class);

        ExceptionResponse exceptionResponse = (ExceptionResponse) response.getBody();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation error");
        assertThat(exceptionResponse.getSubExceptions()).hasSize(2); // 1 field error + 1 global error
    }
}
