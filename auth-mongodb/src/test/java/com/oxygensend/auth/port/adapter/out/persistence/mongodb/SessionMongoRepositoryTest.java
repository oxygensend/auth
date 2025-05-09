package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SessionMongoRepositoryTest {

    @Mock
    private ImportedSessionRepository importedRepository;

    @Mock
    private DataSourceObjectAdapter<Session, SessionMongo> adapter;

    private SessionMongoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SessionMongoRepository(importedRepository, adapter);
    }

    @Test
    @DisplayName("nextIdentity should return a new SessionId with a UUID")
    void nextIdentity_shouldReturnNewId() {
        // When
        SessionId result = repository.nextIdentity();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isNotNull();
    }

    @Test
    @DisplayName("Given a session when save is called then it should be converted and saved")
    void save_shouldConvertAndSaveSession() {
        // Given
        Session session = new Session(new SessionId(UUID.randomUUID()), new UserId(UUID.randomUUID()));
        SessionMongo sessionMongo = new SessionMongo(session.id().value(), session.userId().value());
        
        when(adapter.toDataSource(session)).thenReturn(sessionMongo);
        when(importedRepository.save(sessionMongo)).thenReturn(sessionMongo);
        when(adapter.toDomain(sessionMongo)).thenReturn(session);

        // When
        Session result = repository.save(session);

        // Then
        verify(adapter).toDataSource(session);
        verify(importedRepository).save(sessionMongo);
        verify(adapter).toDomain(sessionMongo);
        assertThat(result).isEqualTo(session);
    }

    @Test
    @DisplayName("Given a userId when removeByUserId is called then the repository should call delete")
    void removeByUserId_shouldCallDelete() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());

        // When
        repository.removeByUserId(userId);

        // Then
        verify(importedRepository).deleteByUserId(userId.value());
    }

    @Test
    @DisplayName("Given a userId when sessionOfUserId is called and session exists then it should return the session")
    void sessionOfUserId_whenSessionExists_shouldReturnSession() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        SessionId sessionId = new SessionId(UUID.randomUUID());
        Session session = new Session(sessionId, userId);
        SessionMongo sessionMongo = new SessionMongo(sessionId.value(), userId.value());

        when(importedRepository.findByUserId(userId.value())).thenReturn(Optional.of(sessionMongo));
        when(adapter.toDomain(sessionMongo)).thenReturn(session);

        // When
        Optional<Session> result = repository.sessionOfUserId(userId);

        // Then
        verify(importedRepository).findByUserId(userId.value());
        verify(adapter).toDomain(sessionMongo);
        assertThat(result).contains(session);
    }

    @Test
    @DisplayName("Given a userId when sessionOfUserId is called and no session exists then it should return empty")
    void sessionOfUserId_whenNoSessionExists_shouldReturnEmpty() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(importedRepository.findByUserId(userId.value())).thenReturn(Optional.empty());

        // When
        Optional<Session> result = repository.sessionOfUserId(userId);

        // Then
        verify(importedRepository).findByUserId(userId.value());
        verify(adapter, never()).toDomain(any());
        assertThat(result).isEmpty();
    }
}
