package com.oxygensend.auth.domain.model.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordTest {

    @Mock
    private PasswordService passwordService;

    @Test
    void shouldCreatePasswordFromPlaintext() {
        // Given
        String plaintext = "password123";
        String hashedValue = "$2a$10$hashed";
        when(passwordService.encode(plaintext)).thenReturn(hashedValue);
        
        // When
        Password password = Password.fromPlaintext(plaintext, passwordService);
        
        // Then
        assertThat(password.hashedValue()).isEqualTo(hashedValue);
        assertThat(password.toString()).isEqualTo(hashedValue);
        verify(passwordService).encode(plaintext);
    }
    
    @Test
    void shouldCreatePasswordFromHashedValue() {
        // Given
        String hashedValue = "$2a$10$hashed";
        
        // When
        Password password = Password.fromHashed(hashedValue);
        
        // Then
        assertThat(password.hashedValue()).isEqualTo(hashedValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenHashedValueIsNullOrEmpty(String invalidValue) {
        // When/Then
        assertThatThrownBy(() -> Password.fromHashed(invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Hashed password cannot be null or empty");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"a", "ab", "abc"})
    void shouldThrowExceptionWhenPlaintextIsTooShort(String shortValue) {
        // When/Then
        assertThatThrownBy(() -> Password.fromPlaintext(shortValue, passwordService))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password must be between 4 and 64 characters");
    }
    
    @Test
    void shouldThrowExceptionWhenPlaintextIsTooLong() {
        // Given
        String longValue = "a".repeat(65);
        
        // When/Then
        assertThatThrownBy(() -> Password.fromPlaintext(longValue, passwordService))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password must be between 4 and 64 characters");
    }
    
    @Test
    void shouldMatchPasswordWhenCorrect() {
        // Given
        String rawPassword = "password123";
        String hashedValue = "$2a$10$hashed";
        when(passwordService.matches(rawPassword, hashedValue)).thenReturn(true);
        Password password = Password.fromHashed(hashedValue);
        
        // When
        boolean matches = password.matches(rawPassword, passwordService);
        
        // Then
        assertThat(matches).isTrue();
        verify(passwordService).matches(rawPassword, hashedValue);
    }
    
    @Test
    void shouldNotMatchPasswordWhenIncorrect() {
        // Given
        String rawPassword = "wrongpassword";
        String hashedValue = "$2a$10$hashed";
        when(passwordService.matches(rawPassword, hashedValue)).thenReturn(false);
        Password password = Password.fromHashed(hashedValue);
        
        // When
        boolean matches = password.matches(rawPassword, passwordService);
        
        // Then
        assertThat(matches).isFalse();
        verify(passwordService).matches(rawPassword, hashedValue);
    }
    
    @Test
    void shouldBeEqualWhenHashedValuesAreEqual() {
        // Given
        Password password1 = Password.fromHashed("$2a$10$same");
        Password password2 = Password.fromHashed("$2a$10$same");
        
        // Then
        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenHashedValuesAreDifferent() {
        // Given
        Password password1 = Password.fromHashed("$2a$10$value1");
        Password password2 = Password.fromHashed("$2a$10$value2");
        
        // Then
        assertThat(password1).isNotEqualTo(password2);
        assertThat(password1.hashCode()).isNotEqualTo(password2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualToNull() {
        // Given
        Password password = Password.fromHashed("$2a$10$hashed");
        
        // Then
        assertThat(password).isNotEqualTo(null);
    }
    
    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        Password password = Password.fromHashed("$2a$10$hashed");
        String string = "$2a$10$hashed";
        
        // Then
        assertThat(password).isNotEqualTo(string);
    }
}
