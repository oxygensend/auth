package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.exception.BadCredentialsException;
import com.oxygensend.auth.domain.model.identity.exception.BlockedUserException;
import com.oxygensend.auth.domain.model.identity.exception.ExpiredCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @Mock
    private EmailAddress email;

    @Mock
    private Username username;

    @Mock
    private UserId userId;

    @Mock
    private BusinessId businessId;
    
    private final String rawPassword = "password123";
    private final Set<Role> roles = Set.of(new Role("USER"));

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(passwordService, userRepository);

        lenient().when(user.id()).thenReturn(userId);
        lenient().when(user.businessId()).thenReturn(businessId);
        lenient().when(user.username()).thenReturn(username);
        lenient().when(user.email()).thenReturn(email);
        lenient().when(user.roles()).thenReturn(roles);
        lenient().when(user.isVerified()).thenReturn(true);
        lenient().when(user.isBlocked()).thenReturn(false);
        lenient().when(user.isCredentialsExpired()).thenReturn(false);
    }

    @Test
    void shouldAuthenticateWithEmail() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.authenticateWithPassword(rawPassword, passwordService)).thenReturn(true);
        when(user.isBlocked()).thenReturn(false);
        when(user.isCredentialsExpired()).thenReturn(false);

        // When
        UserDescriptor result = authenticationService.authenticateWithEmail(email, rawPassword);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.businessId()).isEqualTo(businessId);
        assertThat(result.roles()).isEqualTo(roles);
        assertThat(result.verified()).isTrue();
        verify(user).authenticateWithPassword(rawPassword, passwordService);
    }

    @Test
    void shouldAuthenticateWithUsername() {
        // Given
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(user.authenticateWithPassword(rawPassword, passwordService)).thenReturn(true);
        when(user.isBlocked()).thenReturn(false);
        when(user.isCredentialsExpired()).thenReturn(false);

        // When
        UserDescriptor result = authenticationService.authenticateWithUsername(username, rawPassword);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.businessId()).isEqualTo(businessId);
        assertThat(result.roles()).isEqualTo(roles);
        assertThat(result.verified()).isTrue();
        verify(user).authenticateWithPassword(rawPassword, passwordService);
    }

    @Test
    void shouldRevalidateUser() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.isBlocked()).thenReturn(false);
        when(user.isCredentialsExpired()).thenReturn(false);

        // When
        UserDescriptor result = authenticationService.revalidateUser(userId);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo(username);
        assertThat(result.email()).isEqualTo(email);
        assertThat(result.businessId()).isEqualTo(businessId);
        assertThat(result.roles()).isEqualTo(roles);
        assertThat(result.verified()).isTrue();
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFoundByEmail() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadCredentialsException.class, () -> 
            authenticationService.authenticateWithEmail(email, rawPassword)
        );
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFoundByUsername() {
        // Given
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadCredentialsException.class, () -> 
            authenticationService.authenticateWithUsername(username, rawPassword)
        );
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadCredentialsException.class, () -> 
            authenticationService.revalidateUser(userId)
        );
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenPasswordDoesNotMatch() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.authenticateWithPassword(rawPassword, passwordService)).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> 
            authenticationService.authenticateWithEmail(email, rawPassword)
        );
    }

    @Test
    void shouldThrowBlockedUserExceptionWhenUserIsBlocked() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.authenticateWithPassword(rawPassword, passwordService)).thenReturn(true);
        when(user.isBlocked()).thenReturn(true);

        // When & Then
        assertThrows(BlockedUserException.class, () -> 
            authenticationService.authenticateWithEmail(email, rawPassword)
        );
    }

    @Test
    void shouldThrowExpiredCredentialsExceptionWhenCredentialsExpired() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.authenticateWithPassword(rawPassword, passwordService)).thenReturn(true);
        when(user.isBlocked()).thenReturn(false);
        when(user.isCredentialsExpired()).thenReturn(true);

        // When & Then
        assertThrows(ExpiredCredentialsException.class, () -> 
            authenticationService.authenticateWithEmail(email, rawPassword)
        );
    }
}
