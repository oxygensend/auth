package com.oxygensend.auth.infrastructure.mongo;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("mongo")
interface ImportedSessionRepository extends MongoRepository<SessionMongo, UUID> {
}
