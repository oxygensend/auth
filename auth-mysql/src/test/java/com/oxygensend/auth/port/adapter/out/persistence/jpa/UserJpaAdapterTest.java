package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class UserJpaAdapterTest {

    private final UserJpaAdapter adapter = new UserJpaAdapter();

    @Test
    @DisplayName("Given a UserJpa when toDomain is called then it should return equivalent User domain object")
    void toDomainTest() {
        // Given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String username = "testuser";
        String password = "hashed_password";
        Set<String> roles = Set.of("USER", "ADMIN");
        boolean locked = false;
        boolean verified = true;
        String businessId = "business123";
        AccountActivationType activationType = AccountActivationType.NONE;

        UserJpa userJpa = new UserJpa();
        userJpa.id = id;
        userJpa.email = email;
        userJpa.username = username;
        userJpa.password = password;
        userJpa.roles = roles;
        userJpa.locked = locked;
        userJpa.verified = verified;
        userJpa.businessId = businessId;
        userJpa.accountActivationType = activationType;

        // When
        User user = adapter.toDomain(userJpa);

        // Then
        assertThat(user.id().value()).isEqualTo(id);
        assertThat(user.credentials().email().address()).isEqualTo(email);
        assertThat(user.credentials().username().value()).isEqualTo(username);
        assertThat(user.credentials().password().hashedValue()).isEqualTo(password);
        assertThat(user.roles()).containsExactlyInAnyOrder(new Role("USER"), new Role("ADMIN"));
        assertThat(user.isBlocked()).isEqualTo(locked);
        assertThat(user.isVerified()).isEqualTo(verified);
        assertThat(user.businessId().value()).isEqualTo(businessId);
        assertThat(user.accountActivationType()).isEqualTo(activationType);
    }

    @Test
    @DisplayName("Given a User domain object when toDataSource is called then it should return equivalent UserJpa")
    void toDataSourceTest() {
        // Given
        UUID id = UUID.randomUUID();
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password password = Password.fromHashed("hashed_password");
        Credentials credentials = new Credentials(email, username, password);
        Set<Role> roles = Set.of(new Role("USER"), new Role("ADMIN"));
        boolean locked = false;
        boolean verified = true;
        BusinessId businessId = new BusinessId("business123");
        AccountActivationType activationType = AccountActivationType.NONE;

        User user = new User(
            new UserId(id), credentials, roles, locked, verified, businessId, activationType
        );

        // When
        UserJpa userJpa = adapter.toDataSource(user);

        // Then
        assertThat(userJpa.id).isEqualTo(id);
        assertThat(userJpa.email).isEqualTo("test@example.com");
        assertThat(userJpa.username).isEqualTo("testuser");
        assertThat(userJpa.password).isEqualTo("hashed_password");
        assertThat(userJpa.roles).containsExactlyInAnyOrder("USER", "ADMIN");
        assertThat(userJpa.locked).isEqualTo(locked);
        assertThat(userJpa.verified).isEqualTo(verified);
        assertThat(userJpa.businessId).isEqualTo("business123");
        assertThat(userJpa.accountActivationType).isEqualTo(activationType);
    }
}
