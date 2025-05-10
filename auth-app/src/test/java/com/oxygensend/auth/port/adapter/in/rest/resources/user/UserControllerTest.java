package com.oxygensend.auth.port.adapter.in.rest.resources.user;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.auth.application.identity.RegisterUserCommand;
import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.application.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.application.identity.exception.UserNotFoundException;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.exception.BlockedUserException;
import com.oxygensend.auth.port.adapter.in.rest.exception.ApiExceptionHandler;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.PasswordChangeRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.PasswordResetRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.UserIdRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.VerifyEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.oxygensend.common.domain.model.DomainModelValidationException;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                                 .setValidator(new LocalValidatorFactoryBean())
                                 .setControllerAdvice(new ApiExceptionHandler())
                                 .build();
    }

    @Test
    void shouldDeleteUser_whenValidId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).delete(any(UserId.class));

        // When & Then
        mockMvc.perform(delete("/v1/users/{id}", userId))
               .andExpect(status().isNoContent());

        verify(userService).delete(new UserId(userId));
    }

    @Test
    void shouldReturnNotFound_whenDeleteNonExistentUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doThrow(UserNotFoundException.withId(new UserId(userId)))
            .when(userService).delete(any(UserId.class));

        // When & Then
        mockMvc.perform(delete("/v1/users/{id}", userId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldBlockUser_whenValidId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).block(any(UserId.class));

        // When & Then
        mockMvc.perform(post("/v1/users/{id}/block", userId))
               .andExpect(status().isNoContent());

        verify(userService).block(new UserId(userId));
    }

    @Test
    void shouldReturnNotFound_whenBlockNonExistentUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doThrow(UserNotFoundException.withId(new UserId(userId)))
            .when(userService).block(any(UserId.class));

        // When & Then
        mockMvc.perform(post("/v1/users/{id}/block", userId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldUnblockUser_whenValidId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).unblock(any(UserId.class));

        // When & Then
        mockMvc.perform(post("/v1/users/{id}/unblock", userId))
               .andExpect(status().isNoContent());

        verify(userService).unblock(new UserId(userId));
    }

    @Test
    void shouldReturnNotFound_whenUnblockNonExistentUser() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        doThrow(UserNotFoundException.withId(new UserId(userId)))
            .when(userService).unblock(any(UserId.class));

        // When & Then
        mockMvc.perform(post("/v1/users/{id}/unblock", userId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldVerifyEmail_whenValidToken() throws Exception {
        // Given
        String token = "valid-token";
        VerifyEmailRequest request = new VerifyEmailRequest(token);
        doNothing().when(userService).verifyEmail(anyString());

        // When & Then
        mockMvc.perform(post("/v1/users/verify_email")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNoContent());

        verify(userService).verifyEmail(token);
    }

    @Test
    void shouldGenerateEmailVerificationToken_whenValidUserId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserIdRequest request = new UserIdRequest(userId);
        String token = "generated-token";

        when(userService.generateEmailVerificationToken(any(UserId.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/v1/users/generate_email_verification_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token", is(token)));

        verify(userService).generateEmailVerificationToken(new UserId(userId));
    }

    @Test
    void shouldGeneratePasswordResetToken_whenValidUserId() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserIdRequest request = new UserIdRequest(userId);
        String token = "reset-token";

        when(userService.generatePasswordResetToken(any(UserId.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/v1/users/generate_password_reset_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token", is(token)));

        verify(userService).generatePasswordResetToken(new UserId(userId));
    }

    @Test
    void shouldResetPassword_whenValidRequest() throws Exception {
        // Given
        String token = "valid-reset-token";
        String newPassword = "newPassword123";
        PasswordResetRequest request = new PasswordResetRequest(token, newPassword);

        doNothing().when(userService).resetPassword(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/v1/users/reset_password")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNoContent());

        verify(userService).resetPassword(token, newPassword);
    }

    @Test
    void shouldChangePassword_whenValidRequest() throws Exception {
        // Given
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        PasswordChangeRequest request = new PasswordChangeRequest(newPassword, oldPassword);

        doNothing().when(userService).changePassword(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/v1/users/change_password")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNoContent());

        verify(userService).changePassword(oldPassword, newPassword);
    }

    @Test
    void shouldReturnBadRequest_whenPasswordChangeValidationFails() throws Exception {
        // Given
        PasswordChangeRequest request = new PasswordChangeRequest("", "");

        // When & Then
        mockMvc.perform(post("/v1/users/change_password")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldCreateUser_whenValidRequest() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("USER"),
            "business-id");

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNoContent());

        verify(userService).registerUser(any(RegisterUserCommand.class));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithInvalidRole() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("INVALID_ROLE"),
            "business-id"
        );

        doThrow(new UnexpectedRoleException(new Role("INVALID_ROLE")))
            .when(userService).registerUser(any(RegisterUserCommand.class));

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequest_whenDomainValidationException() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("USER"),
            "business-id"
        );

        doThrow(new DomainModelValidationException("Invalid domain data"))
            .when(userService).registerUser(any(RegisterUserCommand.class));

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message", is("Invalid domain data")));
    }

    @Test
    void shouldReturnForbidden_whenBlockedUserException() throws Exception {
        // Given
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        PasswordChangeRequest request = new PasswordChangeRequest(oldPassword, newPassword);
        UserId userId = new UserId(UUID.randomUUID());

        doThrow(new BlockedUserException(userId))
            .when(userService).changePassword(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/v1/users/change_password")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isForbidden())
               .andExpect(jsonPath("$.message", is("User with id %s is blocked".formatted(userId))));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithNullEmail() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            null,
            "testuser",
            "Password123",
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithBlankEmail() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "",
            "testuser",
            "Password123",
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithNullUsername() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            null,
            "Password123",
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithBlankUsername() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "",
            "Password123",
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithNullPassword() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            null,
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithBlankPassword() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "",
            Set.of("USER"),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithNullRoles() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            null,
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithEmptyRoles() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Collections.emptySet(),
            "business-id"
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithNullBusinessId() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("USER"),
            null
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenCreateUserWithBlankBusinessId() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("USER"),
            ""
        );

        // When & Then
        mockMvc.perform(post("/v1/users/create")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }
}
