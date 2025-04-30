package com.oxygensend.auth.application.settings;

import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Username;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginProviderTest {

    @Mock
    private Credentials credentials;

    @Mock
    private Username userName;

    @Mock
    private EmailAddress emailAddress;

    @Test
    void shouldGetUsernameFromCredentials() {
        // Given
        LoginProvider loginProvider = new LoginProvider(LoginType.USERNAME);
        String usernameValue = "testuser";
        when(credentials.username()).thenReturn(userName);
        when(userName.value()).thenReturn(usernameValue);

        // When
        LoginDto result = loginProvider.get(credentials);

        // Then
        assertThat(result.value()).isEqualTo(usernameValue);
        assertThat(result.type()).isEqualTo(LoginType.USERNAME);
    }

    @Test
    void shouldGetEmailFromCredentials() {
        // Given
        LoginProvider loginProvider = new LoginProvider(LoginType.EMAIL);
        String emailValue = "test@example.com";
        when(credentials.email()).thenReturn(emailAddress);
        when(emailAddress.address()).thenReturn(emailValue);

        // When
        LoginDto result = loginProvider.get(credentials);

        // Then
        assertThat(result.value()).isEqualTo(emailValue);
        assertThat(result.type()).isEqualTo(LoginType.EMAIL);
    }

    @Test
    void shouldCreateLoginDtoFromString() {
        // Given
        LoginProvider loginProvider = new LoginProvider(LoginType.EMAIL);
        String login = "test@example.com";

        // When
        LoginDto result = loginProvider.get(login);

        // Then
        assertThat(result.value()).isEqualTo(login);
        assertThat(result.type()).isEqualTo(LoginType.EMAIL);
    }
}
