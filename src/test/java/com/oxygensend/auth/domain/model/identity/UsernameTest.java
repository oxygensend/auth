package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameTest {

    @Test
    void shouldCreateUsernameSuccessfully() {
        // Given
        String value = "testuser";
        
        // When
        Username username = new Username(value);
        
        // Then
        assertThat(username.value()).isEqualTo(value);
        assertThat(username.toString()).isEqualTo(value);
    }
    
    @Test
    void shouldCreateUsernameFromEmailAddress() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        
        // When
        Username username = new Username(email);
        
        // Then
        assertThat(username.value()).isEqualTo(email.address());
    }
    
    @Test
    void shouldCreateUsernameFromLoginDto() {
        // Given
        String value = "testuser";
        LoginDto loginDto = new LoginDto(value, LoginType.USERNAME);
        
        // When
        Username username = new Username(loginDto);
        
        // Then
        assertThat(username.value()).isEqualTo(value);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenValueIsNullOrEmpty(String invalidValue) {
        // When/Then
        assertThatThrownBy(() -> new Username(invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username cannot be empty");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"abc", "ab", "a"})
    void shouldThrowExceptionWhenValueIsTooShort(String shortValue) {
        // When/Then
        assertThatThrownBy(() -> new Username(shortValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username length invalid");
    }
    
    @Test
    void shouldThrowExceptionWhenValueIsTooLong() {
        // Given
        String longValue = "a".repeat(65);
        
        // When/Then
        assertThatThrownBy(() -> new Username(longValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username length invalid");
    }

    @Test
    void shouldAcceptUsernameAtLowerBoundary() {
        // Given
        String fourCharValue = "abcd";
        
        // When
        Username username = new Username(fourCharValue);
        
        // Then
        assertThat(username.value()).isEqualTo(fourCharValue);
    }
    
    @Test
    void shouldAcceptUsernameAtUpperBoundary() {
        // Given
        String sixtyFourCharValue = "a".repeat(64);
        
        // When
        Username username = new Username(sixtyFourCharValue);
        
        // Then
        assertThat(username.value()).isEqualTo(sixtyFourCharValue);
    }
    
    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        Username username1 = new Username("testuser");
        Username username2 = new Username("testuser");
        
        // Then
        assertThat(username1).isEqualTo(username2);
        assertThat(username1.hashCode()).isEqualTo(username2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        Username username1 = new Username("testuser1");
        Username username2 = new Username("testuser2");
        
        // Then
        assertThat(username1).isNotEqualTo(username2);
        assertThat(username1.hashCode()).isNotEqualTo(username2.hashCode());
    }
}
