package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

interface ImportedUserRepository extends MongoRepository<UserMongo, UUID> {
    Optional<UserMongo> findByEmail(String email);

    Optional<UserMongo> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
