package com.oxygensend.auth.port.adapter.in.rest.filters;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.AccessTokenPayload;
import com.oxygensend.auth.domain.model.identity.UserMother;
import com.oxygensend.auth.port.adapter.in.rest.filters.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Date;

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
    private TokenApplicationService jwtFacade;

    @Mock
    private SecurityContext securityContext;


    private User user;


    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);

        user = UserMother.getRandom();
    }

    @Test
    public void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, java.io.IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtFacade, userDetailsService, securityContext);
    }

    @Test
    public void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, java.io.IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Invalid");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtFacade, userDetailsService, securityContext);
    }

    @Test
    public void testDoFilterInternal_ValidAuthorizationHeader_ExpiredToken()
        throws ServletException, java.io.IOException {

        // Arrange
        String jwtToken = "valid_token";

        var tokenPayload = new AccessTokenPayload(user.username(),
                                                  user.id(),
                                                  user.roles(),
                                                  new Date(), new Date(),
                                                  user.isVerified(),
                                                  user.businessId(),
                                                  user.email());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtFacade.parseToken(jwtToken, TokenType.ACCESS)).thenReturn(tokenPayload);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtFacade).parseToken(jwtToken, TokenType.ACCESS);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    public void testDoFilterInternal_ValidAuthorizationHeader_ValidToken()
        throws ServletException, java.io.IOException {
        // Arrange
        String jwtToken = "valid_token";

        var tokenPayload = new AccessTokenPayload(user.username(),
                                                  user.id(),
                                                  user.roles(),
                                                  new Date(),
                                                  new Date(System.currentTimeMillis() + 3600),
                                                  user.isVerified(),
                                                  user.businessId(),
                                                  user.email());

        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwtToken);
        when(jwtFacade.parseToken(jwtToken, TokenType.ACCESS)).thenReturn(tokenPayload);
        when(userDetailsService.loadUserByUsername(tokenPayload.email().address())).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(null);
        doNothing().when(securityContext).setAuthentication(any(Authentication.class));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtFacade).parseToken(jwtToken, TokenType.ACCESS);
        verify(userDetailsService).loadUserByUsername(tokenPayload.email().address());
        verify(userDetails, times(2)).getAuthorities();
        verify(securityContext).getAuthentication();
        verify(securityContext).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(request, response);
    }

}
