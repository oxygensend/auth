package com.oxygensend.auth.domain.model.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.event.AddedRoleEvent;
import com.oxygensend.auth.domain.model.identity.event.BlockedEvent;
import com.oxygensend.auth.domain.model.identity.event.PasswordChangedEvent;
import com.oxygensend.auth.domain.model.identity.event.PasswordResetedEvent;
import com.oxygensend.auth.domain.model.identity.event.RegisteredEvent;
import com.oxygensend.auth.domain.model.identity.event.RemovedRoleEvent;
import com.oxygensend.auth.domain.model.identity.event.UnblockedEvent;
import com.oxygensend.auth.domain.model.identity.event.VerifiedEvent;
import com.oxygensend.auth.domain.model.identity.exception.PasswordMismatchException;
import com.oxygensend.auth.domain.model.identity.exception.RoleAlreadyExistsException;
import com.oxygensend.auth.domain.model.identity.exception.RoleNotAssignedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private PasswordService passwordService;

    private UserId userId;
    private Credentials credentials;
    private Set<Role> roles;
    private BusinessId businessId;
    private Password password;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        password = Password.fromHashed("hashed_password");
        credentials = new Credentials(
            new EmailAddress("test@example.com"),
            new Username("testuser"),
            password
        );
        roles = new HashSet<>(Set.of(new Role("USER")));
        businessId = new BusinessId("business-123");
    }

    @Test
    void givenValidParameters_whenRegisterNewUser_thenUserIsCreatedWithRegisteredEvent() {
        // Given
        AccountActivationType activationType = AccountActivationType.VERIFY_EMAIL;

        // When
        User user = User.registerNewUser(userId, credentials, roles, businessId, activationType);

        // Then
        assertThat(user.id()).isEqualTo(userId);
        assertThat(user.credentials()).isEqualTo(credentials);
        assertThat(user.roles()).isEqualTo(roles);
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.isVerified()).isFalse();
        assertThat(user.businessId()).isEqualTo(businessId);
        assertThat(user.accountActivationType()).isEqualTo(activationType);

        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(RegisteredEvent.class);
    }

    @Test
    void givenNoActivationType_whenRegisterNewUser_thenUserIsVerified() {
        // Given
        AccountActivationType activationType = AccountActivationType.NONE;

        // When
        User user = User.registerNewUser(userId, credentials, roles, businessId, activationType);

        // Then
        assertThat(user.isVerified()).isTrue();
    }

    @Test
    void givenNewRole_whenAddRole_thenRoleIsAddedWithEvent() {
        // Given
        User user = createDefaultUser();
        Role newRole = new Role("ADMIN");

        // When
        user.addRole(newRole);

        // Then
        assertThat(user.roles()).contains(newRole);
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(AddedRoleEvent.class);

        AddedRoleEvent event = (AddedRoleEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.role()).isEqualTo(newRole);
    }

    @Test
    void givenExistingRole_whenAddRole_thenExceptionIsThrown() {
        // Given
        User user = createDefaultUser();
        Role existingRole = new Role("USER");

        // When/Then
        assertThatThrownBy(() -> user.addRole(existingRole))
            .isInstanceOf(RoleAlreadyExistsException.class);

        assertThat(user.events()).isEmpty();
    }

    @Test
    void givenExistingRole_whenRemoveRole_thenRoleIsRemovedWithEvent() {
        // Given
        User user = createDefaultUser();
        Role existingRole = new Role("USER");

        // When
        user.removeRole(existingRole);

        // Then
        assertThat(user.roles()).doesNotContain(existingRole);
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(RemovedRoleEvent.class);

        RemovedRoleEvent event = (RemovedRoleEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.role()).isEqualTo(existingRole);
    }

    @Test
    void givenNonExistingRole_whenRemoveRole_thenExceptionIsThrown() {
        // Given
        User user = createDefaultUser();
        Role nonExistingRole = new Role("ADMIN");

        // When/Then
        assertThatThrownBy(() -> user.removeRole(nonExistingRole))
            .isInstanceOf(RoleNotAssignedException.class);

        assertThat(user.events()).isEmpty();
    }

    @Test
    void givenUnblockedUser_whenBlock_thenUserIsBlockedWithEvent() {
        // Given
        User user = createDefaultUser();
        assertThat(user.isBlocked()).isFalse();

        // When
        user.block();

        // Then
        assertThat(user.isBlocked()).isTrue();
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(BlockedEvent.class);

        BlockedEvent event = (BlockedEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    void givenBlockedUser_whenBlock_thenExceptionIsThrown() {
        // Given
        User user = createBlockedUser();

        // When/Then
        assertThatThrownBy(user::block)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Account is already blocked");
    }

    @Test
    void givenBlockedUser_whenUnblock_thenUserIsUnblockedWithEvent() {
        // Given
        User user = createBlockedUser();

        // When
        user.unblock();

        // Then
        assertThat(user.isBlocked()).isFalse();
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(UnblockedEvent.class);

        UnblockedEvent event = (UnblockedEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    void givenUnblockedUser_whenUnblock_thenExceptionIsThrown() {
        // Given
        User user = createDefaultUser();

        // When/Then
        assertThatThrownBy(user::unblock)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Account is already unblocked");
    }

    @Test
    void givenCorrectPassword_whenAuthenticateWithPassword_thenReturnsTrue() {
        // Given
        User user = createDefaultUser();
        String plainPassword = "correct_password";
        when(passwordService.matches(eq(plainPassword), any())).thenReturn(true);

        // When
        boolean result = user.authenticateWithPassword(plainPassword, passwordService);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void givenIncorrectPassword_whenAuthenticateWithPassword_thenReturnsFalse() {
        // Given
        User user = createDefaultUser();
        String plainPassword = "wrong_password";
        when(passwordService.matches(eq(plainPassword), any())).thenReturn(false);

        // When
        boolean result = user.authenticateWithPassword(plainPassword, passwordService);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void givenCorrectOldPassword_whenChangePassword_thenPasswordIsChangedWithEvent() {
        // Given
        User user = createDefaultUser();
        String oldPassword = "old_password";
        String newPassword = "new_password";
        String hashedPassword = "hashed_new_password";

        when(passwordService.matches(eq(oldPassword), any())).thenReturn(true);
        when(passwordService.encode(newPassword)).thenReturn(hashedPassword);

        // When
        user.changePassword(oldPassword, newPassword, passwordService);

        // Then
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(PasswordChangedEvent.class);

        PasswordChangedEvent event = (PasswordChangedEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.email()).isEqualTo(credentials.email());
    }

    @Test
    void givenIncorrectOldPassword_whenChangePassword_thenExceptionIsThrown() {
        // Given
        User user = createDefaultUser();
        String oldPassword = "wrong_password";
        String newPassword = "new_password";

        when(passwordService.matches(eq(oldPassword), any())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> user.changePassword(oldPassword, newPassword, passwordService))
            .isInstanceOf(PasswordMismatchException.class)
            .hasMessageContaining("Old password does not match");

        assertThat(user.events()).isEmpty();
    }

    @Test
    void givenNewPassword_whenResetPassword_thenPasswordIsResetWithEvent() {
        // Given
        User user = createDefaultUser();
        String newPassword = "new_password";
        String hashedPassword = "hashed_new_password";

        when(passwordService.encode(newPassword)).thenReturn(hashedPassword);

        // When
        user.resetPassword(newPassword, passwordService);

        // Then
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(PasswordResetedEvent.class);

        PasswordResetedEvent event = (PasswordResetedEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    void givenUnverifiedUserWithChangePasswordActivation_whenResetPassword_thenUserIsVerifiedWithEvents() {
        // Given
        User user = new User(userId, credentials, roles, false, false,
                             businessId, AccountActivationType.CHANGE_PASSWORD);

        String newPassword = "new_password";
        String hashedPassword = "hashed_new_password";

        when(passwordService.encode(newPassword)).thenReturn(hashedPassword);

        // When
        user.resetPassword(newPassword, passwordService);

        // Then
        assertThat(user.isVerified()).isTrue();
        assertThat(user.events()).hasSize(2);

        assertThat(user.events().stream()
                       .anyMatch(event -> event instanceof PasswordResetedEvent)).isTrue();
        assertThat(user.events().stream()
                       .anyMatch(event -> event instanceof VerifiedEvent)).isTrue();
    }

    @Test
    void givenUnverifiedUser_whenVerifyEmail_thenUserIsVerifiedWithEvent() {
        // Given
        User user = new User(userId, credentials, roles, false, false,
                             businessId, AccountActivationType.VERIFY_EMAIL);
        // When
        user.verifyEmail();

        // Then
        assertThat(user.isVerified()).isTrue();
        assertThat(user.events()).hasSize(1);
        assertThat(user.events().iterator().next()).isInstanceOf(VerifiedEvent.class);

        VerifiedEvent event = (VerifiedEvent) user.events().iterator().next();
        assertThat(event.userId()).isEqualTo(userId);
    }

    @Test
    void givenNullId_whenCreateUser_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        assertThatThrownBy(() -> new User(
            null,
            credentials,
            roles,
            false,
            false,
            businessId,
            AccountActivationType.VERIFY_EMAIL
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("UserId cannot be null");
    }

    @Test
    void givenNullCredentials_whenCreateUser_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        assertThatThrownBy(() -> new User(
            userId,
            null,
            roles,
            false,
            false,
            businessId,
            AccountActivationType.VERIFY_EMAIL
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Credentials cannot be null");
    }

    @Test
    void givenNullBusinessId_whenCreateUser_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        assertThatThrownBy(() -> new User(
            userId,
            credentials,
            roles,
            false,
            false,
            null,
            AccountActivationType.VERIFY_EMAIL
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("BusinessId cannot be null");
    }

    @Test
    void givenEmptyRoles_whenCreateUser_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        assertThatThrownBy(() -> new User(
            userId,
            credentials,
            Collections.emptySet(),
            false,
            false,
            businessId,
            AccountActivationType.VERIFY_EMAIL
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Roles cannot be empty");
    }

    @Test
    void givenNullAccountActivationType_whenCreateUser_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        assertThatThrownBy(() -> new User(
            userId,
            credentials,
            roles,
            false,
            false,
            businessId,
            null
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("AccountActivationType cannot be null");
    }

    private User createDefaultUser() {
        return new User(
            userId,
            credentials,
            roles,
            false,
            true,
            businessId,
            AccountActivationType.VERIFY_EMAIL
        );
    }

    private User createBlockedUser() {
        return new User(
            userId,
            credentials,
            roles,
            true,
            true,
            businessId,
            AccountActivationType.VERIFY_EMAIL
        );
    }
}
