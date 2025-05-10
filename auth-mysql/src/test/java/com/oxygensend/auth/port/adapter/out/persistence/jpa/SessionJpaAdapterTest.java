package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class SessionJpaAdapterTest {

    private final SessionJpaAdapter adapter = new SessionJpaAdapter();

    @Test
    @DisplayName("Given a SessionJpa when toDomain is called then it should return equivalent Session domain object")
    void toDomainTest() {
        // Given
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SessionJpa sessionJpa = new SessionJpa(sessionId, userId);

        // When
        Session session = adapter.toDomain(sessionJpa);

        // Then
        assertThat(session.id().value()).isEqualTo(sessionId);
        assertThat(session.userId().value()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Given a Session domain object when toDataSource is called then it should return equivalent SessionJpa")
    void toDataSourceTest() {
        // Given
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Session session = new Session(new SessionId(sessionId), new UserId(userId));

        // When
        SessionJpa sessionJpa = adapter.toDataSource(session);

        // Then
        assertThat(sessionJpa.id).isEqualTo(sessionId);
        assertThat(sessionJpa.userId).isEqualTo(userId);
    }
}
