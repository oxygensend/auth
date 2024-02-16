package com.oxygensend.auth.unit.infrastructure.security;

import com.oxygensend.auth.context.auth.jwt.TokenStorage;
import com.oxygensend.auth.context.auth.jwt.factory.AccessTokenPayloadFactory;
import com.oxygensend.auth.context.auth.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRole;
import com.oxygensend.auth.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenStorage tokenStorage;

    @Mock
    private SecurityContext securityContext;

    private AccessTokenPayloadFactory accessTokenPayloadFactory;

    private User user;


    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
        accessTokenPayloadFactory = new AccessTokenPayloadFactory();

        user = User.builder()
                   .id(UUID.randomUUID())
                   .firstName("John")
                   .lastName("Doe")
                   .email("test@test.com")
                   .password("test123")
                   .roles(Set.of(UserRole.ROLE_ADMIN))
                   .build();
    }

    @Test
    public void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, java.io.IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenStorage, userDetailsService, securityContext);
    }

    @Test
    public void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, java.io.IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Invalid");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenStorage, userDetailsService, securityContext);
    }

    @Test
    public void testDoFilterInternal_ValidAuthorizationHeader_ExpiredToken() throws ServletException, java.io.IOException {

        // Arrange
        String jwtToken = "valid_token";
        AccessTokenPayload tokenPayload = (AccessTokenPayload) accessTokenPayloadFactory.createToken(
                new Date(),
                new Date(),
                user
        );

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(tokenStorage.validate(jwtToken, TokenType.ACCESS)).thenReturn(tokenPayload);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenStorage).validate(jwtToken, TokenType.ACCESS);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    public void testDoFilterInternal_ValidAuthorizationHeader_ValidToken() throws ServletException, java.io.IOException {
        // Arrange
        String jwtToken = "valid_token";
        AccessTokenPayload tokenPayload = (AccessTokenPayload) accessTokenPayloadFactory.createToken(
                new Date(System.currentTimeMillis() + 1000),
                new Date(),
                user
        );

        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(tokenStorage.validate(jwtToken, TokenType.ACCESS)).thenReturn(tokenPayload);
        when(userDetailsService.loadUserByUsername(tokenPayload.email())).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(null);
        doNothing().when(securityContext).setAuthentication(any(Authentication.class));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenStorage).validate(jwtToken, TokenType.ACCESS);
        verify(userDetailsService).loadUserByUsername(tokenPayload.email());
        verify(userDetails, times(2)).getAuthorities();
        verify(securityContext).getAuthentication();
        verify(securityContext).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

}
