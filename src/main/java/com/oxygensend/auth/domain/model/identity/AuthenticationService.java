package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.exception.BadCredentialsException;
import com.oxygensend.auth.domain.model.identity.exception.BlockedUserException;
import com.oxygensend.auth.domain.model.identity.exception.ExpiredCredentialsException;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    public AuthenticationService(PasswordService passwordService,
        UserRepository userRepository) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }

    public UserDescriptor authenticateWithEmail(EmailAddress email, String password) {
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(BadCredentialsException::new);

        return authenticate(user, password);
    }

    public UserDescriptor authenticateWithUsername(Username username, String password) {
        var user = userRepository.findByUsername(username)
                                 .orElseThrow(BadCredentialsException::new);

        return authenticate(user, password);
    }

    public UserDescriptor revalidateUser(UserId userId) {
        var user = userRepository.findById(userId)
                                 .orElseThrow(BadCredentialsException::new);

        validateUser(user);

        return new UserDescriptor(user);
    }

    private UserDescriptor authenticate(User user, String password) {
        if (!user.authenticateWithPassword(password, passwordService)) {
            throw new BadCredentialsException();
        }

        validateUser(user);


        return new UserDescriptor(user);
    }

    private void validateUser(User user) {
        if (user.isBlocked()) {
            throw new BlockedUserException(user.id());
        }
        if (user.isCredentialsExpired()) {
            throw new ExpiredCredentialsException(user.id());
        }
    }
}
