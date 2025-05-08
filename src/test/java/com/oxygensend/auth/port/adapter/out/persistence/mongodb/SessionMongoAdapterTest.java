package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionMongoAdapterTest {

    private final SessionMongoAdapter adapter = new SessionMongoAdapter();

    @Test
    @DisplayName("Given a SessionMongo when toDomain is called then it should return equivalent Session domain object")
    void toDomainTest() {
        // Given
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SessionMongo sessionMongo = new SessionMongo(sessionId, userId);

        // When
        Session session = adapter.toDomain(sessionMongo);

        // Then
        assertThat(session.id().value()).isEqualTo(sessionId);
        assertThat(session.userId().value()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Given a Session domain object when toDataSource is called then it should return equivalent SessionMongo")
    void toDataSourceTest() {
        // Given
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Session session = new Session(new SessionId(sessionId), new UserId(userId));

        // When
        SessionMongo sessionMongo = adapter.toDataSource(session);

        // Then
        assertThat(sessionMongo.id()).isEqualTo(sessionId);
        assertThat(sessionMongo.userId()).isEqualTo(userId);
    }
}
