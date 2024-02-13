package com.oxygensend.auth.unit.context;

import com.oxygensend.auth.context.SessionManager;
import com.oxygensend.auth.context.jwt.TokenStorage;
import com.oxygensend.auth.context.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.context.jwt.payload.TokenPayload;
import com.oxygensend.auth.context.response.AuthenticationResponse;
import com.oxygensend.auth.config.TokenProperties;
import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionManagerTest {

    @InjectMocks
    private SessionManager sessionManager;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private TokenStorage tokenStorage;

    @Mock
    private TokenPayloadFactoryProvider tokenPayloadFactory;

    @Mock
    private TokenProperties tokenProperties;


    @Test
    public void testStartSession_DeleteByIdAndSave() {
        // Arrange
        UUID sessionId = UUID.randomUUID();

        // Act
        sessionManager.startSession(sessionId);

        // Assert
        verify(sessionRepository, times(1)).deleteById(sessionId);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    public void testGetSession_Found() {
        // Arrange
        UUID sessionId = UUID.randomUUID();
        var session = new Session(sessionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act
        var result = sessionManager.getSession(sessionId);

        // Assert
        assertEquals(session, result);
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    public void testGetSession_NotFound() {
        // Arrange
        UUID sessionId = UUID.randomUUID();

        when(sessionRepository.findById(sessionId)).thenThrow(SessionExpiredException.class);

        // Act && assert
        assertThrows(SessionExpiredException.class, () -> sessionManager.getSession(sessionId));
        verify(sessionRepository, times(1)).findById(sessionId);
    }


    @Test
    public void testPrepareSession() {
        // Arrange
        var user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.pl")
                .password("password")
                .build();


        TokenPayload refreshPayload = mock(TokenPayload.class);
        TokenPayload accessPayload = mock(TokenPayload.class);

        String expectedRefreshToken = "valid_refresh_token";
        String expectedAccessToken = "valid_access_token";


        when(tokenPayloadFactory.createToken(eq(TokenType.REFRESH), any(Date.class), any(Date.class), eq(user))).thenReturn(refreshPayload);
        when(tokenPayloadFactory.createToken(eq(TokenType.ACCESS), any(Date.class), any(Date.class), eq(user))).thenReturn(accessPayload);
        when(tokenStorage.generateToken(refreshPayload)).thenReturn(expectedRefreshToken);
        when(tokenStorage.generateToken(accessPayload)).thenReturn(expectedAccessToken);

        // Act
        AuthenticationResponse response = sessionManager.prepareSession(user);

        // Assert
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
        verify(sessionRepository, times(1)).deleteById(any(UUID.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(tokenPayloadFactory, times(1)).createToken(eq(TokenType.REFRESH), any(Date.class), any(Date.class), eq(user));
        verify(tokenPayloadFactory, times(1)).createToken(eq(TokenType.ACCESS), any(Date.class), any(Date.class), eq(user));
        verify(tokenStorage, times(1)).generateToken(refreshPayload);
        verify(tokenStorage, times(1)).generateToken(accessPayload);
    }
}
