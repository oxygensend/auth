package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("mongo")
@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final DataSourceObjectAdapter<User, UserMongo> adapter;


    UserMongoRepository(ImportedUserRepository importedUserRepository,
                        DataSourceObjectAdapter<User, UserMongo> userMongoAdapter) {
        this.importedUserRepository = importedUserRepository;
        this.adapter = userMongoAdapter;
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
    public boolean existsByEmail(EmailAddress email) {
        return importedUserRepository.existsByEmail(email.address());
    }

    @Override
    public boolean existsByUsername(Username username) {
        return importedUserRepository.existsByUsername(username.value());
    }

    @Override
    public void deleteById(UserId id) {
        importedUserRepository.deleteById(id.value());
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return importedUserRepository.findByUsername(username.value()).map(adapter::toDomain);
    }
}
