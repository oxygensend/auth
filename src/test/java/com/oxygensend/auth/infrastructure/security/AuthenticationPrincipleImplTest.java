package com.oxygensend.auth.infrastructure.security;

import com.oxygensend.auth.domain.model.identity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationPrincipleImplTest {


    @Test
    public void testGetAuthenticationPrinciple() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(mockUser);

        SpringSecurityAuthenticationPrinciple authenticationFacade = new SpringSecurityAuthenticationPrinciple();

        // Act
        User result = authenticationFacade.get();

        // Assert
        assertEquals("testuser", result.getUsername());
    }
}

