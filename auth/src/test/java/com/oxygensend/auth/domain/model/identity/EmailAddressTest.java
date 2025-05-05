package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

    @Test
    void shouldCreateEmailAddressSuccessfully() {
        // Given
        String address = "test@example.com";
        
        // When
        EmailAddress email = new EmailAddress(address);
        
        // Then
        assertThat(email.address()).isEqualTo(address);
        assertThat(email.toString()).isEqualTo(address);
    }
    
    @Test
    void shouldCreateEmailAddressFromLoginDto() {
        // Given
        String address = "test@example.com";
        LoginDto loginDto = new LoginDto(address, LoginType.EMAIL);
        
        // When
        EmailAddress email = new EmailAddress(loginDto);
        
        // Then
        assertThat(email.address()).isEqualTo(address);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenAddressIsNullOrEmpty(String invalidAddress) {
        // When/Then
        assertThatThrownBy(() -> new EmailAddress(invalidAddress))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email address cannot be empty");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "plainaddress",
        "@missingusername.com",
        "username@.com",
        "username@domain",
        "username@domain..com"
    })
    void shouldThrowExceptionWhenAddressFormatIsInvalid(String invalidAddress) {
        // When/Then
        assertThatThrownBy(() -> new EmailAddress(invalidAddress))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email address format");
    }
    
    @Test
    void shouldBeEqualWhenAddressesAreEqual() {
        // Given
        EmailAddress email1 = new EmailAddress("test@example.com");
        EmailAddress email2 = new EmailAddress("test@example.com");
        
        // Then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenAddressesAreDifferent() {
        // Given
        EmailAddress email1 = new EmailAddress("test1@example.com");
        EmailAddress email2 = new EmailAddress("test2@example.com");
        
        // Then
        assertThat(email1).isNotEqualTo(email2);
        assertThat(email1.hashCode()).isNotEqualTo(email2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualToNull() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        
        // Then
        assertThat(email).isNotEqualTo(null);
    }
    
    @Test
    void shouldNotBeEqualToDifferentClass() {
        // Given
        EmailAddress email = new EmailAddress("test@example.com");
        String string = "test@example.com";
        
        // Then
        assertThat(email).isNotEqualTo(string);
    }
}
