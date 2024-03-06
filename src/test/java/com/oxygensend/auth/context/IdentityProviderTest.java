package com.oxygensend.auth.context;

import com.oxygensend.auth.config.IdentityType;
import com.oxygensend.auth.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentityProviderTest {


    @Test
    void getIdentity_withUsername() {
        // Arrange
        IdentityProvider identityProvider = new IdentityProvider(IdentityType.USERNAME);
        // Act
        String result = identityProvider.getIdentity(User.builder().username("test").build());
        // Assert
        assertThat(result).isEqualTo("test");
    }

    @Test
    void getIdentity_withEmail() {
        // Arrange
        IdentityProvider identityProvider = new IdentityProvider(IdentityType.EMAIL);
        // Act
        String result = identityProvider.getIdentity(User.builder().email("email@email.com").build());
        // Assert
        assertThat(result).isEqualTo("email@email.com");
    }
}
