package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.GoogleId;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class UserMongoAdapterTest {

    private final UserMongoAdapter adapter = new UserMongoAdapter();

    @Test
    @DisplayName("Given a UserMongo when toDomain is called then it should return equivalent User domain object")
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
        String googleId = "XD";

        UserMongo userMongo = new UserMongo(
                id, email, username, password, roles, locked, verified, businessId, activationType, googleId
        );

        // When
        User user = adapter.toDomain(userMongo);

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
        assertThat(user.googleId().value()).isEqualTo(googleId);
    }

    @Test
    @DisplayName("Given a User domain object when toDataSource is called then it should return equivalent UserMongo")
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
        GoogleId googleId  = new GoogleId("XD");

        User user = new User(
            new UserId(id), credentials, roles, locked, verified, businessId, activationType, googleId
        );

        // When
        UserMongo userMongo = adapter.toDataSource(user);

        // Then
        assertThat(userMongo.id()).isEqualTo(id);
        assertThat(userMongo.email()).isEqualTo("test@example.com");
        assertThat(userMongo.username()).isEqualTo("testuser");
        assertThat(userMongo.password()).isEqualTo("hashed_password");
        assertThat(userMongo.roles()).containsExactlyInAnyOrder("USER", "ADMIN");
        assertThat(userMongo.locked()).isEqualTo(locked);
        assertThat(userMongo.verified()).isEqualTo(verified);
        assertThat(userMongo.businessId()).isEqualTo("business123");
        assertThat(userMongo.accountActivationType()).isEqualTo(activationType);
        assertThat(userMongo.googleId()).isEqualTo(googleId.value());
    }
}
