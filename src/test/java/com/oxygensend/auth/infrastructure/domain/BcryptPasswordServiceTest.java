package com.oxygensend.auth.infrastructure.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BcryptPasswordServiceTest {

    private BcryptPasswordService passwordService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordService = new BcryptPasswordService(passwordEncoder);
    }

    @Test
    void shouldEncodePassword() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encoded_password";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // When
        String result = passwordService.encode(rawPassword);

        // Then
        assertThat(result).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void shouldMatchPasswordWhenCorrect() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encoded_password";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When
        boolean result = passwordService.matches(rawPassword, encodedPassword);

        // Then
        assertThat(result).isTrue();
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void shouldNotMatchPasswordWhenIncorrect() {
        // Given
        String rawPassword = "wrongPassword";
        String encodedPassword = "$2a$10$encoded_password";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // When
        boolean result = passwordService.matches(rawPassword, encodedPassword);

        // Then
        assertThat(result).isFalse();
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}
