package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.application.auth.security.AuthenticationPrinciple;
import com.oxygensend.auth.application.auth.LoginDto;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.AccountActivation;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.UserName;
import com.oxygensend.auth.domain.model.token.EmailVerificationTokenSubject;
import com.oxygensend.auth.domain.model.token.PasswordResetTokenSubject;
import com.oxygensend.auth.domain.model.token.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.domain.model.token.payload.PasswordResetTokenPayload;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.application.MissingUserException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;
import com.oxygensend.auth.application.UserNotFoundException;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final TokenApplicationService tokenApplicationService;
    private final AuthenticationPrinciple authenticationPrinciple;
    private final PasswordService passwordService;

    public Optional<User> userByLogin(LoginDto login) {
        return switch (login.type()) {
            case USERNAME -> repository.findByUsername(new UserName(login));
            case EMAIL -> repository.findByEmail(new EmailAddress(login));
        };
    }
    @Transactional
    public void delete(UserId userId) {
        if (!repository.existsById(userId)) {
            throw new UserNotFoundException("User with %s not found".formatted(userId));
        }
        repository.deleteById(userId);
    }

    @Transactional
    public void block(UserId userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        user.block();
        repository.save(user);
    }

    @Transactional
    public void unblock(UserId userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        user.unblock();
        repository.save(user);
    }

    public String generatePasswordResetToken(UserId userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return tokenApplicationService.createToken(new PasswordResetTokenSubject(user.id()), TokenType.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String token, String password) {
        var resetToken = (PasswordResetTokenPayload) tokenApplicationService.parseToken(token, TokenType.PASSWORD_RESET);
        var user = repository.findById(resetToken.userId())
                             .orElseThrow(() -> new MissingUserException(
                                 "User with id %s not found".formatted(resetToken.userId())));

        user.resetPassword(password, passwordService);
        repository.save(user);
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        var user = authenticationPrinciple.get();

        user.changePassword(oldPassword, newPassword, passwordService);
        repository.save(user);
    }

    public String generateEmailVerificationToken(UserId userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return tokenApplicationService.createToken(new EmailVerificationTokenSubject(user.id()), TokenType.EMAIL_VERIFICATION);
    }

    @Transactional
    public void verifyEmail(String token) {
        var emailToken = (EmailVerificationTokenPayload) tokenApplicationService.parseToken(token, TokenType.EMAIL_VERIFICATION);
        var user = repository.findById(emailToken.userId())
                             .orElseThrow(() -> new MissingUserException(
                                 "User with id %s not found".formatted(emailToken.userId())));

        user.verifyEmail();
        repository.save(user);
    }

    @Transactional
    public void createUser(CreateUserCommand command) {
        repository.findByUsername(command.userName()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var credentials = new Credentials(command.email(),
                                          command.userName(),
                                          Password.fromHashed(passwordService.encode(command.rawPassword())),
                                          false);

        var user = User.registerNewUser(command.userId(),
                                        credentials,
                                        command.roles(),
                                        command.businessId(),
                                        AccountActivation.NONE);

        repository.save(user);
    }
}
