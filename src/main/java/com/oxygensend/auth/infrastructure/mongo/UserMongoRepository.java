package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.domain.IdentityType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongo")
@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final UserMongoAdapter adapter;

    private final IdentityType identity;

    UserMongoRepository(ImportedUserRepository importedUserRepository, UserMongoAdapter userMongoAdapter, SettingsProperties settingsProperties) {
        this.importedUserRepository = importedUserRepository;
        this.adapter = userMongoAdapter;
        this.identity = settingsProperties.identity();
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

    @Override
    public boolean existsById(UUID id) {
        return importedUserRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID uuid) {
        importedUserRepository.deleteById(uuid);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return switch (identity) {
            case USERNAME -> importedUserRepository.findByUsername(username).map(adapter::toDomain);
            case EMAIL -> importedUserRepository.findByEmail(username).map(adapter::toDomain);
        };
    }
}
