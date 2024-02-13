package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.Session;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportedSessionRepository extends MongoRepository<Session, UUID> {
}
