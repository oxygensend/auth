package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SessionMongoRepository implements SessionRepository {

    private final ImportedSessionRepository importedSessionRepository;

    public SessionMongoRepository(ImportedSessionRepository importedSessionRepository) {
        this.importedSessionRepository = importedSessionRepository;
    }

    @Override
    public Session save(Session session) {
        return importedSessionRepository.save(session);
    }

    @Override
    public void deleteById(UUID id) {
        importedSessionRepository.deleteById(id);

    }

    @Override
    public Optional<Session> findById(UUID id) {
        return importedSessionRepository.findById(id);
    }
}
