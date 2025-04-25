package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import com.oxygensend.auth.domain.model.AccountActivation;
import com.oxygensend.auth.domain.model.session.SessionManager;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public RegistrationService(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

   public User registerUser(Credentials credentials,
                      Set<Role> roles,
                      BusinessId businessId,
                      AccountActivation accountActivation) {

        userRepository.findByEmail(credentials.email()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var user = User.registerNewUser(userRepository.nextIdentity(),
                                        credentials,
                                        roles,
                                        businessId,
                                        accountActivation);
        // TODO: publish event

        user = userRepository.save(user);
        sessionManager.startSession(user.id());

        return user;

    }
}
