package com.oxygensend.auth.domain.model.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUniquenessCheckerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @InjectMocks
    private UserUniquenessChecker userUniquenessChecker;

    @Test
    void shouldReturnTrue_whenUserIsUnique() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");

        when(user.email()).thenReturn(email);
        when(user.username()).thenReturn(username);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(false);

        // When
        boolean result = userUniquenessChecker.isUnique(user);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenEmailExists() {
        // Given
        EmailAddress email = new EmailAddress("existing@example.com");

        when(user.email()).thenReturn(email);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = userUniquenessChecker.isUnique(user);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenUsernameExists() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("existinguser");

        when(user.email()).thenReturn(email);
        when(user.username()).thenReturn(username);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        boolean result = userUniquenessChecker.isUnique(user);

        // Then
        assertThat(result).isFalse();
    }


}
