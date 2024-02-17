package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final UserMongoAdapter adapter;

    UserMongoRepository(ImportedUserRepository importedUserRepository, UserMongoAdapter userMongoAdapter) {
        this.importedUserRepository = importedUserRepository;
        this.adapter = userMongoAdapter;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return importedUserRepository.findByEmail(email).map(adapter::toDomain);
    }

    @Override
    public User save(User user) {
        var dataSource = adapter.toDataSource(user);
        return adapter.toDomain(importedUserRepository.save(dataSource));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return importedUserRepository.findById(id).map(adapter::toDomain);
    }
}
