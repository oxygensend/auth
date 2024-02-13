package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;

    public UserMongoRepository(ImportedUserRepository importedUserRepository) {
        this.importedUserRepository = importedUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return importedUserRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return importedUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return importedUserRepository.findById(id);
    }
}
