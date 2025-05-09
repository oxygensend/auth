package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

interface ImportedSessionRepository extends MongoRepository<SessionMongo, UUID> {

    void deleteByUserId(UUID userId);

    Optional<SessionMongo> findByUserId(UUID userId);

}
