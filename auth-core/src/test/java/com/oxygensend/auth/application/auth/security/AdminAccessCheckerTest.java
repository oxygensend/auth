package com.oxygensend.auth.application.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdminAccessCheckerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminAccessChecker adminAccessChecker;

    @Test
    @DisplayName("Given null authentication when isAdmin is called then it should return false")
    void isAdmin_whenAuthenticationIsNull_shouldReturnFalse() {
        // Given
        Authentication authentication = null;

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Given authentication that is not authenticated when isAdmin is called then it should return false")
    void isAdmin_whenAuthenticationIsNotAuthenticated_shouldReturnFalse() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Given authentication with admin role when isAdmin is called then it should return true")
    void isAdmin_whenAuthenticationHasAdminRole_shouldReturnTrue() {
        // Given
        String adminRole = "ADMIN";
        Authentication authentication = mock(Authentication.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(adminRole);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(authority));
        when(roleRepository.isAdminRole(new Role(adminRole))).thenReturn(true);

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isTrue();
        verify(roleRepository).isAdminRole(new Role(adminRole));
    }

    @Test
    @DisplayName("Given authentication without admin role when isAdmin is called then it should return false")
    void isAdmin_whenAuthenticationDoesNotHaveAdminRole_shouldReturnFalse() {
        // Given
        String userRole = "USER";
        Authentication authentication = mock(Authentication.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(authority));
        when(roleRepository.isAdminRole(new Role(userRole))).thenReturn(false);

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isFalse();
        verify(roleRepository).isAdminRole(new Role(userRole));
    }

    @Test
    @DisplayName("Given authentication with multiple roles including admin when isAdmin is called then it should return true")
    void isAdmin_whenAuthenticationHasMultipleRolesIncludingAdmin_shouldReturnTrue() {
        // Given
        String adminRole = "ADMIN";
        String userRole = "USER";
        Authentication authentication = mock(Authentication.class);
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(adminRole);
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority(userRole);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(userAuthority, adminAuthority));
        when(roleRepository.isAdminRole(new Role(userRole))).thenReturn(false);
        when(roleRepository.isAdminRole(new Role(adminRole))).thenReturn(true);

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given authentication with no authorities when isAdmin is called then it should return false")
    void isAdmin_whenAuthenticationHasNoAuthorities_shouldReturnFalse() {
        // Given
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        // When
        boolean result = adminAccessChecker.isAdmin(authentication);

        // Then
        assertThat(result).isFalse();
        verify(roleRepository, never()).isAdminRole(any());
    }
}
