package com.oxygensend.auth.infrastructure.spring.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.helper.UserMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class SpringSecurityAuthenticationPrincipleTest {


    @Test
    public void testGetAuthenticationPrinciple() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        User mockUser = UserMother.getRandom();
        when(authentication.getPrincipal()).thenReturn(mockUser);

        SpringSecurityAuthenticationPrinciple authenticationFacade = new SpringSecurityAuthenticationPrinciple();

        // Act
        User result = authenticationFacade.get();

        // Assert
        Assertions.assertEquals(new Username("username"), result.username());
    }
}

