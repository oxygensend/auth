package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportedUserRepository extends MongoRepository<User, UUID> {



    Optional<User> findByEmail(String email);
}
