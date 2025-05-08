package com.oxygensend.auth.port.adapter.in.rest.resources.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.auth.application.auth.AuthService;
import com.oxygensend.auth.application.auth.AuthenticationTokensDto;
import com.oxygensend.auth.application.auth.RegisterCommand;
import com.oxygensend.auth.application.token.InvalidTokenException;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.exception.BadCredentialsException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import com.oxygensend.auth.port.adapter.in.rest.exception.ApiExceptionHandler;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.AuthenticationRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.RegisterRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                                 .setValidator(new LocalValidatorFactoryBean())
                                 .setControllerAdvice(new ApiExceptionHandler())
                                 .build();
    }

    @Test
    void shouldRegisterUser_whenValidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "testuser",
            "Password123",
            Set.of("USER"),
            "123124"
        );

        UUID userId = UUID.randomUUID();
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        AuthenticationTokensDto tokens = new AuthenticationTokensDto(accessToken, refreshToken);

        when(authService.register(any(RegisterCommand.class)))
            .thenReturn(Pair.of(new UserId(userId), tokens));

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.userId").value(userId.toString()))
               .andExpect(jsonPath("$.accessToken").value(accessToken))
               .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithInvalidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            "",  // invalid email
            "testuser",
            "Password123",
            Set.of("USER")
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnConflict_whenRegisterWithExistingUser() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            "existing@example.com",
            "existing",
            "Password123",
            Set.of("USER")
        );

        when(authService.register(any(RegisterCommand.class)))
            .thenThrow(new UserAlreadyExistsException());

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message").value("User with this username or email already exists."));
    }


    @Test
    void shouldAuthenticate_whenValidCredentials() throws Exception {
        // Given
        String login = "testuser";
        String password = "Password123";
        AuthenticationRequest request = new AuthenticationRequest(login, password);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        var tokens = new AuthenticationTokensDto(accessToken, refreshToken);

        when(authService.authenticate(eq(login), eq(password))).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/v1/auth/access_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").value(accessToken))
               .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        // Given
        String login = "wronguser";
        String password = "wrongpass";
        AuthenticationRequest request = new AuthenticationRequest(login, password);

        when(authService.authenticate(eq(login), eq(password)))
            .thenThrow(new BadCredentialsException());

        // When & Then
        mockMvc.perform(post("/v1/auth/access_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

//    @Test
//    void shouldReturnForbidden_whenUserIsBlocked() throws Exception {
//        // Given
//        String login = "blockeduser";
//        String password = "Password123";
//        AuthenticationRequest request = new AuthenticationRequest(login, password);
//
//        when(authService.authenticate(eq(login), eq(password)))
//            .thenThrow(new BlockedUserException());
//
//        // When & Then
//        mockMvc.perform(post("/v1/auth/access_token")
//                            .contentType(APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//               .andExpect(status().isForbidden())
//               .andExpect(jsonPath("$.message").value("User is blocked"));
//    }

    @Test
    void shouldRefreshToken_whenValidToken() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        String accessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        var tokens = new AuthenticationTokensDto(accessToken, newRefreshToken);

        when(authService.refreshToken(eq(refreshToken))).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").value(accessToken))
               .andExpect(jsonPath("$.refreshToken").value(newRefreshToken));
    }

    @Test
    void shouldReturnUnauthorized_whenInvalidRefreshToken() throws Exception {
        // Given
        String invalidToken = "invalid-token";
        RefreshTokenRequest request = new RefreshTokenRequest(invalidToken);

        when(authService.refreshToken(eq(invalidToken)))
            .thenThrow(new InvalidTokenException());

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.message").value("Invalid token"));
    }

    @Test
    void shouldValidateToken_whenUserAttributesArePresent() throws Exception {
        // Given
        String userId = UUID.randomUUID().toString();
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/validate_token")
                            .requestAttr("X-USER-ID", userId)
                            .requestAttr("X-AUTHORITIES", authorities))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.userId").value(userId))
               .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"))
               .andExpect(jsonPath("$.authorities[1].authority").value("ROLE_ADMIN"));
    }

    @Test
    void shouldValidateToken_whenAttributesAreMissing() throws Exception {
        // When & Then
        mockMvc.perform(post("/v1/auth/validate_token"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.userId").doesNotExist())
               .andExpect(jsonPath("$.authorities").isEmpty());
    }

    // Request validation tests

    @Test
    void shouldReturnBadRequest_whenRegisterWithNullEmail() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            null,
            "testuser",
            "Password123",
            Set.of("USER")
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithNullUsername() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            null,
            "Password123",
            Set.of("USER")
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterWithNullPassword() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "testuser",
            null,
            Set.of("USER")
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }


    @Test
    void shouldReturnBadRequest_whenAuthenticateWithNullLogin() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
            null,
            "Password123"
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/access_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenAuthenticateWithNullPassword() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
            "testuser",
            null
        );

        // When & Then
        mockMvc.perform(post("/v1/auth/access_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenRefreshTokenWithNullToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldReturnBadRequest_whenRefreshTokenWithBlankToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("");

        // When & Then
        mockMvc.perform(post("/v1/auth/refresh_token")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }
}
