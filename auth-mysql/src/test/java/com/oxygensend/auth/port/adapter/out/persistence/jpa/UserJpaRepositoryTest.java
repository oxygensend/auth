package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import com.oxygensend.common.domain.model.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserJpaRepositoryTest {

    @Mock
    private ImportedUserRepository importedRepository;

    @Mock
    private DomainEventJpaRepository domainEventJpaRepository;

    @Mock
    private UserJpaAdapter adapter;

    @InjectMocks
    private UserJpaRepository repository;

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
        UserJpa userJpa = createUserJpa();
        User user = mock(User.class);

        when(importedRepository.findByEmail(email.address())).thenReturn(Optional.of(userJpa));
        when(adapter.toDomain(userJpa)).thenReturn(user);

        // When
        Optional<User> result = repository.findByEmail(email);

        // Then
        verify(importedRepository).findByEmail(email.address());
        verify(adapter).toDomain(userJpa);
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
        User user = mock(User.class);
        List<DomainEvent> events = List.of(new TestDomainEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            "data"
        ));
        UserJpa userJpa = createUserJpa();

        when(user.events()).thenReturn(events);
        when(adapter.toDataSource(user)).thenReturn(userJpa);
        when(importedRepository.save(userJpa)).thenReturn(userJpa);
        when(adapter.toDomain(userJpa)).thenReturn(user);

        // When
        User result = repository.save(user);

        // Then
        verify(adapter).toDataSource(user);
        verify(importedRepository).save(userJpa);
        verify(adapter).toDomain(userJpa);
        verify(domainEventJpaRepository).saveAndPublish(events);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("Given a userId when findById is called and user exists then it should return the user")
    void findById_whenUserExists_shouldReturnUser() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        UserJpa userJpa = createUserJpa();
        User user = createUser();

        when(importedRepository.findById(userId.value())).thenReturn(Optional.of(userJpa));
        when(adapter.toDomain(userJpa)).thenReturn(user);

        // When
        Optional<User> result = repository.findById(userId);

        // Then
        verify(importedRepository).findById(userId.value());
        verify(adapter).toDomain(userJpa);
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
        UserJpa userJpa = createUserJpa();
        User user = mock(User.class);

        when(importedRepository.findByUsername(username.value())).thenReturn(Optional.of(userJpa));
        when(adapter.toDomain(userJpa)).thenReturn(user);

        // When
        Optional<User> result = repository.findByUsername(username);

        // Then
        verify(importedRepository).findByUsername(username.value());
        verify(adapter).toDomain(userJpa);
        assertThat(result).contains(user);
    }

    private UserJpa createUserJpa() {
        UserJpa userJpa = new UserJpa();
        userJpa.id = UUID.randomUUID();
        userJpa.email = "test@example.com";
        userJpa.username = "testuser";
        userJpa.password = "password";
        userJpa.roles = Set.of("USER");
        userJpa.locked = false;
        userJpa.verified = true;
        userJpa.businessId = "business123";
        userJpa.accountActivationType = AccountActivationType.NONE;
        return userJpa;
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
