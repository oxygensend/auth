package com.oxygensend.auth.domain.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.exception.SessionExpiredException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SessionManagerTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionManager sessionManager;

    @Test
    public void testStartSession() {
        UserId userId = new UserId(UUID.randomUUID());
        SessionId sessionId = new SessionId(UUID.randomUUID());

        when(sessionRepository.nextIdentity()).thenReturn(sessionId);
        doNothing().when(sessionRepository).removeByUserId(userId);
        when(sessionRepository.save(any(Session.class))).thenReturn(new Session(sessionId, userId));

        SessionId result = sessionManager.startSession(userId);

        assertEquals(sessionId, result);
        verify(sessionRepository).removeByUserId(userId);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    public void testCurrentSessionExists() {
        UserId userId = new UserId(UUID.randomUUID());
        SessionId sessionId = new SessionId(UUID.randomUUID());
        Session session = new Session(sessionId, userId);
        when(sessionRepository.sessionOfUserId(userId)).thenReturn(Optional.of(session));

        Session result = sessionManager.currentSession(userId);
        assertEquals(session, result);
    }

    @Test
    public void testCurrentSessionExpired() {
        UserId userId = new UserId(UUID.randomUUID());
        when(sessionRepository.sessionOfUserId(userId)).thenReturn(Optional.empty());

        assertThrows(SessionExpiredException.class, () -> sessionManager.currentSession(userId));
    }
}
