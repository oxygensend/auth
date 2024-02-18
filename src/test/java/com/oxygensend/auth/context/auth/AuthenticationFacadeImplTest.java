package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.domain.User;
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
public class AuthenticationFacadeImplTest {


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

        AuthenticationFacadeImpl authenticationFacade = new AuthenticationFacadeImpl();

        // Act
        User result = authenticationFacade.getAuthenticationPrinciple();

        // Assert
        assertEquals("testuser", result.getUsername());
    }
}

