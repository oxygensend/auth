package com.oxygensend.auth.infrastructure.persistence.mongodb;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("MONGODB")
interface ImportedSessionRepository extends MongoRepository<SessionMongo, UUID> {

    void deleteByUserId(UUID userId);

   Optional<SessionMongo> findByUserId(UUID userId);

}
