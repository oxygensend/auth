package com.oxygensend.auth.domain.model.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialsTest {

    @Mock
    private PasswordService passwordService;

    @Test
    void shouldCreateCredentialsSuccessfully() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password password = Password.fromHashed("$2a$10$hashed");
        
        // When
        Credentials credentials = new Credentials(email, username, password);
        
        // Then
        assertThat(credentials.email()).isEqualTo(email);
        assertThat(credentials.username()).isEqualTo(username);
        assertThat(credentials.password()).isEqualTo(password);
        assertThat(credentials.expired()).isFalse();
    }
    
    @Test
    void shouldCreateCredentialsWithExpired() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password password = Password.fromHashed("$2a$10$hashed");
        
        // When
        Credentials credentials = new Credentials(email, username, password, true);
        
        // Then
        assertThat(credentials.expired()).isTrue();
        assertThat(credentials.isNonExpired()).isFalse();
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        Username username = new Username("testuser");
        Password password = Password.fromHashed("$2a$10$hashed");
        
        // When/Then
        assertThatThrownBy(() -> new Credentials(null, username, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be null");
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        
        // When/Then
        assertThatThrownBy(() -> new Credentials(email, username, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Password password = Password.fromHashed("$2a$10$hashed");

        // When/Then
        assertThatThrownBy(() -> new Credentials(email, null, password))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Expired cannot be null");
    }
    
    @Test
    void shouldCreateNewCredentialsWithChangedPassword() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password oldPassword = Password.fromHashed("$2a$10$old");
        Credentials credentials = new Credentials(email, username, oldPassword);
        Password newPassword = Password.fromHashed("$2a$10$new");
        
        // When
        Credentials updatedCredentials = credentials.passwordChanged(newPassword);
        
        // Then
        assertThat(updatedCredentials.password()).isEqualTo(newPassword);
        assertThat(updatedCredentials.email()).isEqualTo(credentials.email());
        assertThat(updatedCredentials.username()).isEqualTo(credentials.username());
        assertThat(updatedCredentials.expired()).isEqualTo(credentials.expired());
    }
    
    @Test
    void shouldMatchPasswordWhenCorrect() {
        // Given
        String rawPassword = "password123";
        Password password = Password.fromHashed("$2a$10$hashed");
        Credentials credentials = new Credentials(
            new EmailAddress("test@example.com"),
            new Username("testuser"),
            password
        );
        
        when(passwordService.matches(rawPassword, password.hashedValue())).thenReturn(true);
        
        // When
        boolean matches = credentials.passwordMatches(rawPassword, passwordService);
        
        // Then
        assertThat(matches).isTrue();
    }
    
    @Test
    void shouldNotMatchPasswordWhenIncorrect() {
        // Given
        String rawPassword = "wrongpassword";
        Password password = Password.fromHashed("$2a$10$hashed");
        Credentials credentials = new Credentials(
            new EmailAddress("test@example.com"),
            new Username("testuser"),
            password
        );
        
        when(passwordService.matches(rawPassword, password.hashedValue())).thenReturn(false);
        
        // When
        boolean matches = credentials.passwordMatches(rawPassword, passwordService);
        
        // Then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldHaveCorrectEqualityBehavior() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        Username username = new Username("testuser");
        Password password = Password.fromHashed("$2a$10$hashed");

        Credentials credentials1 = new Credentials(email, username, password);
        Credentials credentials2 = new Credentials(email, username, password);
        Credentials differentCredentials = new Credentials(email, username, Password.fromHashed("$2a$10$different"));

        // Then - record equality is based on all fields
        assertThat(credentials1).isEqualTo(credentials2);
        assertThat(credentials1).isNotEqualTo(differentCredentials);
        assertThat(credentials1.hashCode()).isEqualTo(credentials2.hashCode());
        assertThat(credentials1.hashCode()).isNotEqualTo(differentCredentials.hashCode());
    }


}
