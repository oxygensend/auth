//package com.oxygensend.auth.infrastructure.jpa;
//
//import com.oxygensend.auth.domain.auth.model.Session;
//import com.oxygensend.auth.domain.auth.model.SessionRepository;
//import java.util.Optional;
//import java.util.UUID;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class SessionJpaRepository implements SessionRepository {
//
//    private final ImportedSessionRepository importedSessionRepository;
//    private final SessionJpaAdapter adapter;
//
//    SessionJpaRepository(ImportedSessionRepository importedSessionRepository, SessionJpaAdapter adapter) {
//        this.importedSessionRepository = importedSessionRepository;
//        this.adapter = adapter;
//    }
//
//    @Override
//    public Session save(Session session) {
//        var dataSource = adapter.toDataSource(session);
//        return adapter.toDomain(importedSessionRepository.save(dataSource));
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        importedSessionRepository.deleteById(id);
//    }
//
//    @Override
//    public Optional<Session> findById(UUID id) {
//        return importedSessionRepository.findById(id).map(adapter::toDomain);
//    }
//}
