package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Profile(Ports.MONGODB)
@Repository
public class UserMongoRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final DomainEventMongoRepository domainEventMongoRepository;
    private final DataSourceObjectAdapter<User, UserMongo> adapter;


    UserMongoRepository(ImportedUserRepository importedUserRepository,
                        DomainEventMongoRepository domainEventMongoRepository,
                        DataSourceObjectAdapter<User, UserMongo> userMongoAdapter) {
        this.importedUserRepository = importedUserRepository;
        this.domainEventMongoRepository = domainEventMongoRepository;
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
        var savedUser = adapter.toDomain(importedUserRepository.save(dataSource));
        domainEventMongoRepository.saveAndPublish(user.events());
        return savedUser;
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
