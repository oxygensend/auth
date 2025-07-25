package com.oxygensend.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.GoogleId;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class OauthUserTest {

    private static final UserId USER_ID = new UserId(UUID.randomUUID());
    private static final Set<Role> DEFAULT_ROLES = Set.of(new Role("USER"));
    private static final Credentials CREDENTIALS = new Credentials(
        new EmailAddress("test@example.com"),
        new Username("testuser")
    );
    private static final BusinessId BUSINESS_ID = new BusinessId("business-123");
    private static final GoogleId GOOGLE_ID = new GoogleId("google-456");

    @Test
    void registerUser_shouldCreateOauthUserWithCorrectProperties() {
        // When
        OauthUser user = OauthUser.registerUser(USER_ID, DEFAULT_ROLES, CREDENTIALS, BUSINESS_ID, GOOGLE_ID);

        // Then
        assertThat(user.id()).isEqualTo(USER_ID);
        assertThat(user.credentials()).isEqualTo(CREDENTIALS);
        assertThat(user.roles()).isEqualTo(DEFAULT_ROLES);
        assertThat(user.businessId()).isEqualTo(BUSINESS_ID);
        assertThat(user.googleId()).isEqualTo(GOOGLE_ID);
        assertThat(user.accountActivationType()).isEqualTo(AccountActivationType.GOOGLE_OAUTH);
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.isVerified()).isTrue();
    }

    @Test
    void registerUser_withNullBusinessId_shouldCreateUserWithNullBusinessId() {
        // When
        OauthUser user = OauthUser.registerUser(USER_ID, DEFAULT_ROLES, CREDENTIALS, null, GOOGLE_ID);

        // Then
        assertThat(user.id()).isEqualTo(USER_ID);
        assertThat(user.credentials()).isEqualTo(CREDENTIALS);
        assertThat(user.roles()).isEqualTo(DEFAULT_ROLES);
        assertThat(user.businessId()).isNull();
        assertThat(user.googleId()).isEqualTo(GOOGLE_ID);
        assertThat(user.accountActivationType()).isEqualTo(AccountActivationType.GOOGLE_OAUTH);
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.isVerified()).isTrue();
    }

    @Test
    void constructor_shouldCreateOauthUserWithProvidedProperties() {
        // Given
        boolean isLocked = true;
        boolean isVerified = false;
        AccountActivationType activationType = AccountActivationType.VERIFY_EMAIL;

        // When
        OauthUser user = new OauthUser(USER_ID, CREDENTIALS, DEFAULT_ROLES, isLocked, isVerified, 
                                      BUSINESS_ID, activationType, GOOGLE_ID);

        // Then
        assertThat(user.id()).isEqualTo(USER_ID);
        assertThat(user.credentials()).isEqualTo(CREDENTIALS);
        assertThat(user.roles()).isEqualTo(DEFAULT_ROLES);
        assertThat(user.businessId()).isEqualTo(BUSINESS_ID);
        assertThat(user.googleId()).isEqualTo(GOOGLE_ID);
        assertThat(user.accountActivationType()).isEqualTo(activationType);
        assertThat(user.isBlocked()).isEqualTo(isLocked);
        assertThat(user.isVerified()).isEqualTo(isVerified);
    }
}