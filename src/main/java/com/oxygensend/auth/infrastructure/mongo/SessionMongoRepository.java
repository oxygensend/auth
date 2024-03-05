package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongo")
@Repository
public class SessionMongoRepository implements SessionRepository {

    private final ImportedSessionRepository importedSessionRepository;
    private final SessionMongoAdapter adapter;

    SessionMongoRepository(ImportedSessionRepository importedSessionRepository, SessionMongoAdapter adapter) {
        this.importedSessionRepository = importedSessionRepository;
        this.adapter = adapter;
    }

    @Override
    public Session save(Session session) {
        var dataSource = adapter.toDataSource(session);
        return adapter.toDomain(importedSessionRepository.save(dataSource));
    }

    @Override
    public void deleteById(UUID id) {
        importedSessionRepository.deleteById(id);
    }

    @Override
    public Optional<Session> findById(UUID id) {
        return importedSessionRepository.findById(id).map(adapter::toDomain);
    }
}
