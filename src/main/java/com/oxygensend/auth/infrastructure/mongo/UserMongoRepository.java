package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.application.auth.LoginType;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongo")
@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final UserMongoAdapter adapter;

    private final LoginType identity;

    UserMongoRepository(ImportedUserRepository importedUserRepository, UserMongoAdapter userMongoAdapter, SettingsProperties settingsProperties) {
        this.importedUserRepository = importedUserRepository;
        this.adapter = userMongoAdapter;
        this.identity = settingsProperties.identity();
    }

    @Override
    public UserId nextIdentity() {
       return new UserId(UUID.randomUUID());
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return importedUserRepository.findByEmail(email.address()).map(adapter::toDomain);
    }

    @Override
    public User save(User user) {
        var dataSource = adapter.toDataSource(user);
        return adapter.toDomain(importedUserRepository.save(dataSource));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return importedUserRepository.findById(id.value()).map(adapter::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return importedUserRepository.existsById(id.value());
    }

    @Override
    public void deleteById(UserId id) {
        importedUserRepository.deleteById(id.value());
    }

    @Override
    public Optional<User> findByUsername(UserName username) {
        return switch (identity) {
            case USERNAME -> importedUserRepository.findByUsername(username.value()).map(adapter::toDomain);
            case EMAIL -> importedUserRepository.findByEmail(username.value()).map(adapter::toDomain);
        };
    }
}
