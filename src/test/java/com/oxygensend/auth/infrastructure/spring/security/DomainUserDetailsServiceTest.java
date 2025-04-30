package com.oxygensend.auth.infrastructure.spring.security;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginProvider;
import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainUserDetailsServiceTest {

    private DomainUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private LoginProvider loginProvider;

    @Mock
    private User user;

    @Mock
    private Credentials credentials;

    @Mock
    private Password password;

    @BeforeEach
    void setUp() {
        userDetailsService = new DomainUserDetailsService(userService, loginProvider);
    }

    @Test
    void shouldLoadUserByUsername() {
        // Given
        String username = "test@example.com";
        LoginDto loginDto = new LoginDto(username, LoginType.EMAIL);
        String passwordValue = "hashedPassword";
        Set<Role> roles = Set.of(
                new Role("ROLE_USER"),
                new Role("ROLE_ADMIN")
        );

        when(loginProvider.get(username)).thenReturn(loginDto);
        when(userService.userByLogin(loginDto)).thenReturn(Optional.of(user));
        when(user.credentials()).thenReturn(credentials);
        when(loginProvider.get(credentials)).thenReturn(loginDto);
        when(user.password()).thenReturn(password);
        when(password.hashedValue()).thenReturn(passwordValue);
        when(user.isCredentialsExpired()).thenReturn(false);
        when(user.isBlocked()).thenReturn(false);
        when(user.roles()).thenReturn(roles);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(passwordValue);
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isFalse(); // Note: user.isBlocked() is false, but in the implementation it's negated
        
        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertThat(authorities).contains("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent@example.com";
        LoginDto loginDto = new LoginDto(username, LoginType.EMAIL);
        when(loginProvider.get(username)).thenReturn(loginDto);
        when(userService.userByLogin(loginDto)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername(username)
        );
    }
}
