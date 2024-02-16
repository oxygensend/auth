package com.oxygensend.auth.unit.context.user_role;


import com.oxygensend.auth.context.user_role.UserRoleRequest;
import com.oxygensend.auth.context.user_role.UserRoleService;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.UserRole;
import com.oxygensend.auth.domain.exception.RoleAlreadyExistsException;
import com.oxygensend.auth.domain.exception.RoleNotAssignedException;
import com.oxygensend.auth.helper.UserMother;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    public void addRoleToUser_roleNotExists_addsRole() {
        // Arrange
        var user = UserMother.getRandom();
        var request = new UserRoleRequest(user.id(), UserRole.ROLE_ADMIN);
        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));

        // Act
        userRoleService.addRoleToUser(request);

        // Assert
        verify(userRepository, times(1)).save(user.withNewRole(UserRole.ROLE_ADMIN));
    }

    @Test
    public void addRoleToUser_roleExists_throwsException() {
        // Arrange
        var user = UserMother.getRandom().withNewRole(UserRole.ROLE_ADMIN);
        var request = new UserRoleRequest(user.id(), UserRole.ROLE_ADMIN);
        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RoleAlreadyExistsException.class, () -> userRoleService.addRoleToUser(request));
    }

    @Test
    public void removeRoleFromUser_roleExists_removesRole() {
        // Arrange
        var user = UserMother.getRandom().withNewRole(UserRole.ROLE_ADMIN);
        var request = new UserRoleRequest(user.id(), UserRole.ROLE_ADMIN);
        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));

        // Act
        userRoleService.removeRoleFromUser(request);

        // Assert
        verify(userRepository, times(1)).save(user.withoutRole(UserRole.ROLE_ADMIN));
    }

    @Test
    public void removeRoleFromUser_roleNotExists_throwsException() {
        // Arrange
        var user = UserMother.getRandom();
        var request = new UserRoleRequest(user.id(), UserRole.ROLE_ADMIN);
        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RoleNotAssignedException.class, () -> userRoleService.removeRoleFromUser(request));
    }
}