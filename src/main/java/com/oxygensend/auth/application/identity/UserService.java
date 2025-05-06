package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.application.auth.security.AuthenticationPrinciple;
import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.RegistrationService;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.identity.exception.UserNotFoundException;
import com.oxygensend.auth.domain.model.session.SessionRepository;
import com.oxygensend.auth.domain.model.token.EmailVerificationTokenSubject;
import com.oxygensend.auth.domain.model.token.PasswordResetTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.domain.model.token.payload.PasswordResetTokenPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final TokenApplicationService tokenApplicationService;
    private final AuthenticationPrinciple authenticationPrinciple;
    private final PasswordService passwordService;
    private final RegistrationService registrationService;
    private final SessionRepository sessionRepository;


    public UserService(UserRepository repository, TokenApplicationService tokenApplicationService,
                       AuthenticationPrinciple authenticationPrinciple, PasswordService passwordService,
                       RegistrationService registrationService, SessionRepository sessionRepository) {
        this.repository = repository;
        this.tokenApplicationService = tokenApplicationService;
        this.authenticationPrinciple = authenticationPrinciple;
        this.passwordService = passwordService;
        this.registrationService = registrationService;
        this.sessionRepository = sessionRepository;
    }

    public Optional<User> userByLogin(LoginDto login) {
        LOGGER.info("Finding user by login: {}", login);
        return switch (login.type()) {
            case USERNAME -> repository.findByUsername(new Username(login));
            case EMAIL -> repository.findByEmail(new EmailAddress(login));
        };
    }

    @Transactional
    public void delete(UserId userId) {
        LOGGER.info("Deleting user with ID: {}", userId);
        if (!repository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
        repository.deleteById(userId);
        sessionRepository.removeByUserId(userId);
        LOGGER.info("User with ID {} deleted successfully", userId);
    }

    @Transactional
    public void block(UserId userId) {
        LOGGER.info("Blocking user with ID: {}", userId);
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.block();
        repository.save(user);
        LOGGER.info("User with ID {} blocked successfully", userId);
    }

    @Transactional
    public void unblock(UserId userId) {
        LOGGER.info("Unblocking user with ID: {}", userId);
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.unblock();
        repository.save(user);
        LOGGER.info("User with ID {} unblocked successfully", userId);
    }

    public String generatePasswordResetToken(UserId userId) {
        LOGGER.info("Generating password reset token for user with ID: {}", userId);
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return tokenApplicationService.createToken(new PasswordResetTokenSubject(user.id()), TokenType.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String token, String password) {
        LOGGER.info("Resetting password for user with token: {}", token);
        var resetToken =
            (PasswordResetTokenPayload) tokenApplicationService.parseToken(token, TokenType.PASSWORD_RESET);

        var user = repository.findById(resetToken.userId())
                             .orElseThrow(() -> UserNotFoundException.withId(resetToken.userId()));

        user.resetPassword(password, passwordService);
        repository.save(user);
        LOGGER.info("Password reset successfully for user with ID {}", resetToken.userId());

    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        LOGGER.info("Changing password for authenticated user");
        var user = authenticationPrinciple.get();

        user.changePassword(oldPassword, newPassword, passwordService);
        repository.save(user);
        LOGGER.info("Password changed successfully for user with ID {}", user.id());
    }

    public String generateEmailVerificationToken(UserId userId) {
        LOGGER.info("Generating email verification token for user with ID: {}", userId);
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return tokenApplicationService.createToken(new EmailVerificationTokenSubject(user.id()),
                                                   TokenType.EMAIL_VERIFICATION);
    }

    @Transactional
    public void verifyEmail(String token) {
        LOGGER.info("Verifying email for user with token: {}", token);
        var emailToken =
            (EmailVerificationTokenPayload) tokenApplicationService.parseToken(token, TokenType.EMAIL_VERIFICATION);
        var user = repository.findById(emailToken.userId())
                             .orElseThrow(() -> UserNotFoundException.withId(emailToken.userId()));

        user.verifyEmail();
        repository.save(user);
    }

    @Transactional
    public void createUser(CreateUserCommand command) {
        LOGGER.info("Creating user with email: {}", command.email());
        var credentials = new Credentials(command.email(),
                                          command.username(),
                                          Password.fromHashed(passwordService.encode(command.rawPassword())),
                                          false);

        registrationService.registerUser(credentials,
                                         command.roles(),
                                         command.businessId(),
                                         AccountActivationType.NONE);
        LOGGER.info("User with email {} created successfully", command.email());
    }
}
