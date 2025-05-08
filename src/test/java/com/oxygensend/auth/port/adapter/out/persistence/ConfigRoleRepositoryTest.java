package com.oxygensend.auth.port.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.infrastructure.app_config.properties.SettingsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ConfigRoleRepositoryTest {

    @Mock
    private SettingsProperties settingsProperties;

    private ConfigRoleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ConfigRoleRepository(settingsProperties);
    }

    @Test
    @DisplayName("Given roles in settings when findAll is called then it should return all roles as Role objects")
    void findAll_shouldReturnAllRoles() {
        // Given
        List<String> roleNames = List.of("USER", "ADMIN", "MANAGER");
        when(settingsProperties.allRoles()).thenReturn(roleNames);

        // When
        List<Role> result = repository.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(
            new Role("USER"),
            new Role("ADMIN"),
            new Role("MANAGER")
        );
    }

    @Test
    @DisplayName("Given an existing role when exists is called then it should return true")
    void exists_whenRoleExists_shouldReturnTrue() {
        // Given
        List<String> roleNames = List.of("USER", "ADMIN", "MANAGER");
        when(settingsProperties.allRoles()).thenReturn(roleNames);
        Role role = new Role("ADMIN");

        // When
        boolean result = repository.exists(role);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given a non-existing role when exists is called then it should return false")
    void exists_whenRoleDoesNotExist_shouldReturnFalse() {
        // Given
        List<String> roleNames = List.of("USER", "ADMIN", "MANAGER");
        when(settingsProperties.allRoles()).thenReturn(roleNames);
        Role role = new Role("SUPERVISOR");

        // When
        boolean result = repository.exists(role);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Given an admin role when isAdminRole is called then it should return true")
    void isAdminRole_whenRoleIsAdmin_shouldReturnTrue() {
        // Given
        List<String> adminRoles = List.of("ADMIN", "SUPER_ADMIN");
        when(settingsProperties.adminRoles()).thenReturn(adminRoles);
        Role role = new Role("ADMIN");

        // When
        boolean result = repository.isAdminRole(role);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Given a non-admin role when isAdminRole is called then it should return false")
    void isAdminRole_whenRoleIsNotAdmin_shouldReturnFalse() {
        // Given
        List<String> adminRoles = List.of("ADMIN", "SUPER_ADMIN");
        when(settingsProperties.adminRoles()).thenReturn(adminRoles);
        Role role = new Role("USER");

        // When
        boolean result = repository.isAdminRole(role);

        // Then
        assertThat(result).isFalse();
    }
}
