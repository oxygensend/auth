package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.port.Ports;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile(Ports.MONGODB)
interface ImportedSessionRepository extends MongoRepository<SessionMongo, UUID> {

    void deleteByUserId(UUID userId);

   Optional<SessionMongo> findByUserId(UUID userId);

}
