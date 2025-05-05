package com.oxygensend.auth.application.identity;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;
import com.oxygensend.auth.domain.model.identity.UserMother;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.exception.RoleAlreadyExistsException;
import com.oxygensend.auth.domain.model.identity.exception.RoleNotAssignedException;
import com.oxygensend.auth.domain.model.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.domain.model.identity.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    public void addRoleToUser_validRole_addsRole() {
        // Arrange
        var user = UserMother.getRandom();
        var role = new Role("ROLE_USER");

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(true);

        // Act
        userRoleService.addRoleToUser(user.id(), role);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void addRoleToUser_userNotFound_throwsException() {
        // Arrange
        var userId = UserMother.userId();
        var role = new Role("ROLE_ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userRoleService.addRoleToUser(userId, role));
    }

    @Test
    public void addRoleToUser_invalidRole_throwsException() {
        // Arrange
        var user = UserMother.getRandom();
        var role = new Role("ADMIN");

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(false);

        // Act & Assert
        assertThrows(UnexpectedRoleException.class, () -> userRoleService.addRoleToUser(user.id(), role));
    }

    @Test
    public void addRoleToUser_roleAlreadyExists_throwsException() {
        // Arrange
        var user = UserMother.getRandom();
        var role = new Role("ROLE_ADMIN"); // Assume user already has USER role from UserMother.getRandom()

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(true);

        // Act & Assert
        assertThrows(RoleAlreadyExistsException.class, () -> userRoleService.addRoleToUser(user.id(), role));
    }

    @Test
    public void removeRoleFromUser_validRole_removesRole() {
        // Arrange
        var user = UserMother.getRandom(); // Assume user has USER role from UserMother.getRandom()
        var role = new Role("ROLE_ADMIN");

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(true);

        // Act
        userRoleService.removeRoleFromUser(user.id(), role);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void removeRoleFromUser_userNotFound_throwsException() {
        // Arrange
        var userId = UserMother.userId();
        var role = new Role("ROLE_USER");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userRoleService.removeRoleFromUser(userId, role));
    }

    @Test
    public void removeRoleFromUser_invalidRole_throwsException() {
        // Arrange
        var user = UserMother.getRandom();
        var role = new Role("ADMIN");

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(false);

        // Act & Assert
        assertThrows(UnexpectedRoleException.class, () -> userRoleService.removeRoleFromUser(user.id(), role));
    }

    @Test
    public void removeRoleFromUser_roleNotAssigned_throwsException() {
        // Arrange
        var user = UserMother.getRandom(); // Assume user has only USER role
        var role = new Role("ROLE_USER"); // Role not assigned

        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(roleRepository.exists(role)).thenReturn(true);

        // Act & Assert
        assertThrows(RoleNotAssignedException.class, () -> userRoleService.removeRoleFromUser(user.id(), role));
    }
}
