package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.domain.model.identity.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@UserRoleFeatureEnabled
@Service
public class UserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void addRoleToUser(UserId userId, Role role) {
        var user = getUser(userId);
        validateRole(role);
        user.addRole(role);

        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(UserId userId, Role role) {
        var user = getUser(userId);
        validateRole(role);
        user.removeRole(role);

        userRepository.save(user);
    }

    private User getUser(UserId userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));
    }

    private void validateRole(Role role) {
        if (!roleRepository.exists(role)) {
            throw new UnexpectedRoleException(role);
        }
    }

}
