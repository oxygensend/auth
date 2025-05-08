package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.port.Ports;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile(Ports.MONGODB)
interface ImportedUserRepository extends MongoRepository<UserMongo, UUID> {
    Optional<UserMongo> findByEmail(String email);

    Optional<UserMongo> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
