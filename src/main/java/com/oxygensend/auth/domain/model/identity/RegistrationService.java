package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.exception.UnexpectedRoleException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RegistrationService(UserRepository userRepository,
                               RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User registerUser(Credentials credentials,
                             Set<Role> roles,
                             BusinessId businessId,
                             AccountActivationType accountActivation) {

        checkIfUserExists(credentials);
        checkIfRolesExist(roles);

        var user = User.registerNewUser(userRepository.nextIdentity(),
                                        credentials,
                                        roles,
                                        businessId,
                                        accountActivation);
        return userRepository.save(user);
    }


    public void checkIfUserExists(Credentials credentials) {
        if (userRepository.existsByEmail(credentials.email())) {
            throw UserAlreadyExistsException.withEmail();
        }

        if (userRepository.existsByUsername(credentials.username())) {
            throw UserAlreadyExistsException.withUsername();
        }
    }

    private void checkIfRolesExist(Set<Role> roles) {
        List<Role> notFoundRoles = roles.stream()
                                        .filter(role -> !roleRepository.exists(role))
                                        .toList();

        if (!notFoundRoles.isEmpty()) {
            throw new UnexpectedRoleException(notFoundRoles);
        }

    }
}
