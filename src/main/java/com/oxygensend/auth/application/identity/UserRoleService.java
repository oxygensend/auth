package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.application.MissingUserException;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@UserRoleEndpointEnabled
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRepository userRepository;

    @Transactional
    public void addRoleToUser(UserId userId, Role role) {
        var user = getUser(userId);
        user.addRole(role);

        userRepository.save(user);
    }

    @Transactional
    public void removeRoleFromUser(UserId userId, Role role) {
        var user = getUser(userId);
        user.removeRole(role);

        userRepository.save(user);
    }

    private User getUser(UserId userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new MissingUserException("User with %s not found".formatted(userId)));
    }

}
