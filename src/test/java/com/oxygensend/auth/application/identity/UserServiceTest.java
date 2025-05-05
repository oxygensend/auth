package com.oxygensend.auth.application.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.application.auth.security.AuthenticationPrinciple;
import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.RegistrationService;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserMother;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.identity.exception.UserNotFoundException;
import com.oxygensend.auth.domain.model.session.SessionRepository;
import com.oxygensend.auth.domain.model.token.EmailVerificationTokenSubject;
import com.oxygensend.auth.domain.model.token.PasswordResetTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.domain.model.token.payload.PasswordResetTokenPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private TokenApplicationService tokenApplicationService;

    @Mock
    private AuthenticationPrinciple authenticationPrinciple;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldFindUserByUsername_whenUsernameLoginProvided() {
        // Given
        Username username = new Username("testuser");
        LoginDto loginDto = new LoginDto("testuser", LoginType.USERNAME);
        User expectedUser = UserMother.getRandom();

        when(repository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = service.userByLogin(loginDto);

        // Then
        assertThat(result).contains(expectedUser);
    }

    @Test
    void shouldFindUserByEmail_whenEmailLoginProvided() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        LoginDto loginDto = new LoginDto("test@example.com", LoginType.EMAIL);
        User expectedUser = UserMother.getRandom();

        when(repository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = service.userByLogin(loginDto);

        // Then
        assertThat(result).contains(expectedUser);
    }

    @Test
    void shouldDeleteUser_whenUserExists() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(repository.existsById(userId)).thenReturn(true);

        // When
        service.delete(userId);

        // Then
        verify(repository).deleteById(userId);
        verify(sessionRepository).removeByUserId(userId);
    }

    @Test
    void shouldThrowUserNotFoundException_whenDeletingNonExistingUser() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(repository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.delete(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(userId.toString());
    }

    @Test
    void shouldBlockUser_whenUserExists() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getMocked();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When
        service.block(userId);

        // Then
        verify(user).block();
        verify(repository).save(user);
    }

    @Test
    void shouldThrowUserNotFoundException_whenBlockingNonExistingUser() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.block(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(userId.toString());
    }

    @Test
    void shouldUnblockUser_whenUserExists() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getMocked();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When
        service.unblock(userId);

        // Then
        verify(user).unblock();
        verify(repository).save(user);
    }

    @Test
    void shouldThrowUserNotFoundException_whenUnblockingNonExistingUser() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.unblock(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining(userId.toString());
    }

    @Test
    void shouldGeneratePasswordResetToken_whenUserExists() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getRandom();
        String expectedToken = "reset-token";

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenApplicationService.createToken(any(PasswordResetTokenSubject.class), eq(TokenType.PASSWORD_RESET)))
            .thenReturn(expectedToken);

        // When
        String resultToken = service.generatePasswordResetToken(userId);

        // Then
        assertThat(resultToken).isEqualTo(expectedToken);
        verify(tokenApplicationService).createToken(any(PasswordResetTokenSubject.class), eq(TokenType.PASSWORD_RESET));
    }

    @Test
    void shouldResetPassword_whenValidTokenProvided() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getMocked();
        String token = "valid-token";
        String newPassword = "newPassword123";
        PasswordResetTokenPayload payload = mock(PasswordResetTokenPayload.class);
        when(payload.userId()).thenReturn(userId);

        when(tokenApplicationService.parseToken(token, TokenType.PASSWORD_RESET)).thenReturn(payload);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When
        service.resetPassword(token, newPassword);

        // Then
        verify(user).resetPassword(eq(newPassword), eq(passwordService));
        verify(repository).save(user);
    }

    @Test
    void shouldChangePassword_whenAuthenticatedUser() {
        // Given
        User user = UserMother.getMocked();
        String oldPassword = "oldPassword";
        String newPassword = "newPassword123";

        when(authenticationPrinciple.get()).thenReturn(user);

        // When
        service.changePassword(oldPassword, newPassword);

        // Then
        verify(user).changePassword(eq(oldPassword), eq(newPassword), eq(passwordService));
        verify(repository).save(user);
    }

    @Test
    void shouldGenerateEmailVerificationToken_whenUserExists() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getRandom();
        String expectedToken = "verification-token";

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenApplicationService.createToken(any(EmailVerificationTokenSubject.class),
                                                 eq(TokenType.EMAIL_VERIFICATION)))
            .thenReturn(expectedToken);

        // When
        String resultToken = service.generateEmailVerificationToken(userId);

        // Then
        assertThat(resultToken).isEqualTo(expectedToken);
        verify(tokenApplicationService).createToken(any(EmailVerificationTokenSubject.class),
                                                    eq(TokenType.EMAIL_VERIFICATION));
    }

    @Test
    void shouldVerifyEmail_whenValidTokenProvided() {
        // Given
        UserId userId = new UserId(UUID.randomUUID());
        User user = UserMother.getMocked();
        String token = "valid-token";
        EmailVerificationTokenPayload payload = mock(EmailVerificationTokenPayload.class);
        when(payload.userId()).thenReturn(userId);

        when(tokenApplicationService.parseToken(token, TokenType.EMAIL_VERIFICATION)).thenReturn(payload);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // When
        service.verifyEmail(token);

        // Then
        verify(user).verifyEmail();
        verify(repository).save(user);
    }

    @Test
    void shouldCreateUser_whenValidCommandProvided() {
        // Given
        CreateUserCommand command = new CreateUserCommand(
            new EmailAddress("test@example.com"),
            new Username("testuser"),
            "password123",
            Set.of(new Role("USER")),
            true,
            new BusinessId("business-id")
        );

        String encodedPassword = "encoded-password";
        when(passwordService.encode(command.rawPassword())).thenReturn(encodedPassword);

        // When
        service.createUser(command);

        // Then
        verify(passwordService).encode(command.rawPassword());
        verify(registrationService).registerUser(
            any(Credentials.class),
            eq(command.roles()),
            eq(command.businessId()),
            eq(AccountActivationType.NONE)
        );
    }
}
