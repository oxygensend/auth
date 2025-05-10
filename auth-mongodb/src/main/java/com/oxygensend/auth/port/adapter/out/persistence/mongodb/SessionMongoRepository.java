package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import com.oxygensend.auth.domain.model.session.SessionRepository;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SessionMongoRepository implements SessionRepository {

    private final ImportedSessionRepository importedSessionRepository;

    private final DataSourceObjectAdapter<Session, SessionMongo> adapter;

    SessionMongoRepository(ImportedSessionRepository importedSessionRepository,
                           DataSourceObjectAdapter<Session, SessionMongo> adapter) {
        this.importedSessionRepository = importedSessionRepository;
        this.adapter = adapter;
    }

    @Override
    public SessionId nextIdentity() {
        return new SessionId(UUID.randomUUID());
    }

    @Override
    public Session save(Session session) {
        var dataSource = adapter.toDataSource(session);
        return adapter.toDomain(importedSessionRepository.save(dataSource));
    }

    @Override
    public void removeByUserId(UserId id) {
        importedSessionRepository.deleteByUserId(id.value());
    }

    @Override
    public Optional<Session> sessionOfUserId(UserId userId) {
        return importedSessionRepository.findByUserId(userId.value()).map(adapter::toDomain);
    }


}
