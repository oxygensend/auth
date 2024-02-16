package com.oxygensend.auth.unit.context.auth;

import com.oxygensend.auth.context.auth.AuthService;
import com.oxygensend.auth.context.auth.SessionManager;
import com.oxygensend.auth.context.auth.jwt.TokenStorage;
import com.oxygensend.auth.context.auth.jwt.payload.RefreshTokenPayload;
import com.oxygensend.auth.context.auth.request.AuthenticationRequest;
import com.oxygensend.auth.context.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.context.auth.request.RegisterRequest;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.context.auth.response.ValidationResponse;
import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import com.oxygensend.auth.domain.exception.TokenException;
import com.oxygensend.auth.domain.exception.UnauthorizedException;
import com.oxygensend.auth.domain.exception.UserAlreadyExistsException;
import com.oxygensend.auth.helper.ValidationResponseMother;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenStorage tokenStorage;

    @Mock
    private SessionManager sessionManager;


    @Test
    public void testAuthenticate_ValidCredentials() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        var user = User.builder()
                       .id(UUID.randomUUID())
                       .firstName("John")
                       .lastName("Doe")
                       .email(email)
                       .password(passwordEncoder.encode(password))
                       .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken(user, null, null));
        when(sessionManager.prepareSession(user)).thenReturn(new AuthenticationResponse("access_token", "refresh_token"));

        // Act
        AuthenticationResponse response = authService.authenticate(request);

        // Assert
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(sessionManager, times(1)).prepareSession(user);
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        AuthenticationRequest request = new AuthenticationRequest(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(request));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoMoreInteractions(sessionManager);
    }

    @Test
    public void testRegister_NewUser() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        RegisterRequest request = new RegisterRequest("John", "Doe", email, password);
        AuthenticationResponse expectedResponse = new AuthenticationResponse("access_token", "refresh_token");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mock(User.class));
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User savedUser = invocation.getArgument(0);
//            savedUser.id(UUID.randomUUID());
//            return savedUser;
//        });

        when(sessionManager.prepareSession(any(User.class))).thenReturn(expectedResponse);


        // Act
        AuthenticationResponse response = authService.register(request);

        // Assert
        assertEquals(response, expectedResponse);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
        verify(sessionManager, times(1)).prepareSession(any(User.class));
    }


    @Test
    public void testRegister_ExistingUser() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        RegisterRequest request = new RegisterRequest("John", "Doe", email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(passwordEncoder, userRepository, sessionManager, tokenStorage);
    }


    @Test
    public void test_RefreshToken() {

        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token");
        AuthenticationResponse expectedResponse = new AuthenticationResponse("access_token", "refresh_token");

        var id = UUID.randomUUID();
        RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload(
                id,
                new Date(),
                new Date(System.currentTimeMillis() + 1000)
        );
        Session session = new Session(id);

        when(tokenStorage.validate(anyString(), any(TokenType.class))).thenReturn(refreshTokenPayload);
        when(sessionManager.getSession(id)).thenReturn(session);
        when(userRepository.findById(id)).thenReturn(Optional.of(mock(User.class)));
        when(sessionManager.prepareSession(any(User.class))).thenReturn(expectedResponse);

        // Act
        AuthenticationResponse response = authService.refreshToken(request);

        // Assert
        assertEquals(response, expectedResponse);
        verify(tokenStorage, times(1)).validate(anyString(), any(TokenType.class));
        verify(userRepository, times(1)).findById(id);
        verify(sessionManager, times(1)).getSession(id);
        verify(sessionManager, times(1)).prepareSession(any(User.class));

    }

    @Test
    public void test_RefreshToken_SessionNotFoundException() {

        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token");

        var id = UUID.randomUUID();
        RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload(
                id,
                new Date(),
                new Date(System.currentTimeMillis() + 1000)
        );
        Session session = new Session(id);

        when(tokenStorage.validate(anyString(), any(TokenType.class))).thenReturn(refreshTokenPayload);
        when(sessionManager.getSession(id)).thenReturn(session);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        assertThrows(SessionExpiredException.class, () -> authService.refreshToken(request));

    }

    @Test
    public void test_RefreshToken_TokenExpired() {

        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token");

        var id = UUID.randomUUID();
        RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload(
                id,
                new Date(),
                new Date(System.currentTimeMillis() - 1000)
        );

        when(tokenStorage.validate(anyString(), any(TokenType.class))).thenReturn(refreshTokenPayload);

        // Act
        assertThrows(TokenException.class, () -> authService.refreshToken(request));

    }

    @Test
    public void test_ValidateToken_expectAuthorized() {

        // Arrange
        ValidationResponse expectedResponse = ValidationResponseMother.authorized();

        // Act
        ValidationResponse response = authService.validateToken(expectedResponse.userId(), expectedResponse.authorities());

        assertEquals(response, expectedResponse);
    }

    @Test
    public void test_ValidateToken_expectUnauthorized() {

        // Arrange
        ValidationResponse expectedResponse = ValidationResponseMother.unAuthorized();

        // Act
        ValidationResponse response = authService.validateToken(expectedResponse.userId(), expectedResponse.authorities());

        assertEquals(response, expectedResponse);
    }


}