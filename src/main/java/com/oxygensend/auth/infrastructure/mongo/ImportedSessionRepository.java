package com.oxygensend.auth.infrastructure.mongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

interface ImportedSessionRepository extends MongoRepository<SessionMongo, UUID> {
}
