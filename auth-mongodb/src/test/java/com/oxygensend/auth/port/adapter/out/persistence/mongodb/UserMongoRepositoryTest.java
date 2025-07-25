package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import com.oxygensend.common.domain.model.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserMongoRepositoryTest {

    @Mock
    private ImportedUserRepository importedRepository;

    @Mock
    private DomainEventMongoRepository domainEventMongoRepository;

    @Mock
    private DataSourceObjectAdapter<User, UserMongo> adapter;

    private UserMongoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserMongoRepository(importedRepository, domainEventMongoRepository, adapter);
    }

    @Test
    @DisplayName("nextIdentity should return a new UserId with a UUID")
    void nextIdentity_shouldReturnNewId() {
        // When
        UserId result = repository.nextIdentity();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.value()).isNotNull();
    }

    @Test
    @DisplayName("Given an email when findByEmail is called and user exists then it should return the user")
    void findByEmail_whenUserExists_shouldReturnUser() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        UserMongo userMongo = createUserMongo();
        User user = createUser();

        when(importedRepository.findByEmail(email.address())).thenReturn(Optional.of(userMongo));
        when(adapter.toDomain(userMongo)).thenReturn(user);

        // When
        Optional<User> result = repository.findByEmail(email);

        // Then
        verify(importedRepository).findByEmail(email.address());
        verify(adapter).toDomain(userMongo);
        assertThat(result).contains(user);
    }

    @Test
    @DisplayName("Given an email when findByEmail is called and user does not exist then it should return empty")
    void findByEmail_whenNoUserExists_shouldReturnEmpty() {
        // Given
        EmailAddress email = new EmailAddress("nonexistent@example.com");
        when(importedRepository.findByEmail(email.address())).thenReturn(Optional.empty());

        // When
        Optional<User> result = repository.findByEmail(email);

        // Then
        verify(importedRepository).findByEmail(email.address());
        verify(adapter, never()).toDomain(any());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Given a user when save is called then it should save the user and publish its events")
    void save_shouldSaveUserAndPublishEvents() {
        // Given
        User user = UserMother.getMocked();
        List<DomainEvent> events = List.of(new TestDomainEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            "data"
        ));
        UserMongo userMongo = createUserMongo();

        when(user.events()).thenReturn(events);
        when(adapter.toDataSource(user)).thenReturn(userMongo);
        when(importedRepository.save(userMongo)).thenReturn(userMongo);
        when(adapter.toDomain(userMongo)).thenReturn(user);

        // When
        User result = repository.save(user);

        // Then
        verify(adapter).toDataSource(user);
        verify(importedRepository).save(userMongo);
        verify(adapter).toDomain(userMongo);
        verify(domainEventMongoRepository).saveAndPublish(events);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Given a userId when findById is called and user exists then it should return the user")
    void findById_whenUserExists_shouldReturnUser() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        UserMongo userMongo = createUserMongo();
        User user = createUser();

        when(importedRepository.findById(userId.value())).thenReturn(Optional.of(userMongo));
        when(adapter.toDomain(userMongo)).thenReturn(user);

        // When
        Optional<User> result = repository.findById(userId);

        // Then
        verify(importedRepository).findById(userId.value());
        verify(adapter).toDomain(userMongo);
        assertThat(result).contains(user);
    }

    @Test
    @DisplayName("Given a userId when existsById is called then it should return the result from imported repository")
    void existsById_shouldDelegateToImportedRepository() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(importedRepository.existsById(userId.value())).thenReturn(true);

        // When
        boolean result = repository.existsById(userId);

        // Then
        verify(importedRepository).existsById(userId.value());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given an email when existsByEmail is called then it should return the result from imported repository")
    void existsByEmail_shouldDelegateToImportedRepository() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        when(importedRepository.existsByEmail(email.address())).thenReturn(true);

        // When
        boolean result = repository.existsByEmail(email);

        // Then
        verify(importedRepository).existsByEmail(email.address());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given a username when existsByUsername is called then it should return the result from imported repository")
    void existsByUsername_shouldDelegateToImportedRepository() {
        // Given
        Username username = new Username("testuser");
        when(importedRepository.existsByUsername(username.value())).thenReturn(true);

        // When
        boolean result = repository.existsByUsername(username);

        // Then
        verify(importedRepository).existsByUsername(username.value());
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given a userId when deleteById is called then it should delegate to imported repository")
    void deleteById_shouldDelegateToImportedRepository() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());

        // When
        repository.deleteById(userId);

        // Then
        verify(importedRepository).deleteById(userId.value());
    }

    @Test
    @DisplayName("Given a username when findByUsername is called and user exists then it should return the user")
    void findByUsername_whenUserExists_shouldReturnUser() {
        // Given
        Username username = new Username("testuser");
        UserMongo userMongo = createUserMongo();
        User user = createUser();

        when(importedRepository.findByUsername(username.value())).thenReturn(Optional.of(userMongo));
        when(adapter.toDomain(userMongo)).thenReturn(user);

        // When
        Optional<User> result = repository.findByUsername(username);

        // Then
        verify(importedRepository).findByUsername(username.value());
        verify(adapter).toDomain(userMongo);
        assertThat(result).contains(user);
    }

    private UserMongo createUserMongo() {
        return new UserMongo(
            UUID.randomUUID(),
            "test@example.com",
            "testuser",
            "password",
            Set.of("USER"),
            false,
            true,
            "business123",
            AccountActivationType.NONE,
            null
        );
    }

    private User createUser() {
        UUID id = UUID.randomUUID();
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password password = Password.fromHashed("password");
        Credentials credentials = new Credentials(email, username, password);
        Set<Role> roles = Set.of(new Role("USER"));
        BusinessId businessId = new BusinessId("business123");

        return new User(
            new UserId(id),
            credentials,
            roles,
            false,
            true,
            businessId,
            AccountActivationType.NONE,
            null
        );
    }
}
