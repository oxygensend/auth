package com.oxygensend.auth.infrastructure.persistence.mongodb;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("MONGODB")
interface ImportedUserRepository extends MongoRepository<UserMongo, UUID> {
    Optional<UserMongo> findByEmail(String email);

    Optional<UserMongo> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
