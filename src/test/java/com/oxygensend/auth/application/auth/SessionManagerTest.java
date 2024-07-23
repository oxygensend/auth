package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.application.jwt.JwtFacade;
import com.oxygensend.auth.application.auth.response.AuthenticationResponse;
import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
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
    private JwtFacade jwtFacade;


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
                       .email("john@doe.pl")
                       .password("password")
                       .build();


        String expectedRefreshToken = "valid_refresh_token";
        String expectedAccessToken = "valid_access_token";


        when(jwtFacade.generateToken(eq(user), eq(TokenType.REFRESH))).thenReturn(expectedRefreshToken);
        when(jwtFacade.generateToken(eq(user), eq(TokenType.ACCESS))).thenReturn(expectedAccessToken);

        // Act
        AuthenticationResponse response = sessionManager.prepareSession(user);

        // Assert
        assertEquals(expectedAccessToken, response.accessToken());
        assertEquals(expectedRefreshToken, response.refreshToken());
        verify(sessionRepository, times(1)).deleteById(any(UUID.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(jwtFacade, times(1)).generateToken(eq(user), eq(TokenType.REFRESH));
        verify(jwtFacade, times(1)).generateToken(eq(user), eq(TokenType.ACCESS));
    }
}
