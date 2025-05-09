package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.port.Ports;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserJpaRepository implements UserRepository {

    private final ImportedUserRepository importedUserRepository;
    private final DomainEventJpaRepository domainEventJpaRepository;
    private final UserJpaAdapter adapter;


    UserJpaRepository(ImportedUserRepository importedUserRepository, DomainEventJpaRepository domainEventJpaRepository,
                      UserJpaAdapter userMongoAdapter) {
        this.importedUserRepository = importedUserRepository;
        this.domainEventJpaRepository = domainEventJpaRepository;
        this.adapter = userMongoAdapter;
    }

    @Override
    public UserId nextIdentity() {
        return new UserId(java.util.UUID.randomUUID());
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return importedUserRepository.findByEmail(email.address()).map(adapter::toDomain);
    }

    @Override
    public User save(User user) {
        var dataSource = adapter.toDataSource(user);
        var savedUser = adapter.toDomain(importedUserRepository.save(dataSource));
        domainEventJpaRepository.saveAndPublish(user.events());
        return savedUser;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return importedUserRepository.findById(id.value()).map(adapter::toDomain);
    }

    @Override
    public boolean existsById(UserId uuid) {
        return importedUserRepository.existsById(uuid.value());
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
    public void deleteById(UserId uuid) {
        importedUserRepository.deleteById(uuid.value());
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return importedUserRepository.findByUsername(username.value()).map(adapter::toDomain);
    }
}
