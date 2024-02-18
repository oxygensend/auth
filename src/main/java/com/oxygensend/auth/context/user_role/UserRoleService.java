package com.oxygensend.auth.context.user_role;

import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.MissingUserException;
import com.oxygensend.auth.domain.exception.RoleAlreadyExistsException;
import com.oxygensend.auth.domain.exception.RoleNotAssignedException;
import com.oxygensend.auth.infrastructure.settings.UserRoleEndpointEnabled;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@UserRoleEndpointEnabled
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRepository userRepository;

    public void addRoleToUser(UserRoleRequest request) {
        var user = getUser(request.userId());
        var newRole = request.role();

        user.roles().stream()
            .filter(role -> role == newRole)
            .findAny()
            .ifPresentOrElse(
                    role -> {
                        throw new RoleAlreadyExistsException();
                    },
                    () -> {
                        userRepository.save(user.withNewRole(newRole));
                    }
            );
    }


    public void removeRoleFromUser(UserRoleRequest request) {
        var user = getUser(request.userId());
        var roleToRemove = request.role();

        user.roles().stream()
            .filter(role -> role == roleToRemove)
            .findAny()
            .ifPresentOrElse(
                    role -> {
                        userRepository.save(user.withoutRole(roleToRemove));
                    },
                    () -> {
                        throw new RoleNotAssignedException();
                    }
            );
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new MissingUserException("User with %s not found".formatted(userId)));
    }
}
