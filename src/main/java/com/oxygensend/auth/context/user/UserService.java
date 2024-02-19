package com.oxygensend.auth.context.user;

import com.oxygensend.auth.context.SendMailCommand;
import com.oxygensend.auth.context.auth.AuthenticationFacade;
import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.context.jwt.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.context.jwt.payload.PasswordResetTokenPayload;
import com.oxygensend.auth.context.auth.request.PasswordChangeRequest;
import com.oxygensend.auth.context.auth.request.PasswordResetRequest;
import com.oxygensend.auth.context.auth.request.VerifyEmailRequest;
import com.oxygensend.auth.domain.NotificationRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.MissingUserException;
import com.oxygensend.auth.domain.exception.PasswordMismatchException;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.oxygensend.auth.domain.NotificationMessages.EMAIL_VERIFICATION_MESSAGE;
import static com.oxygensend.auth.domain.NotificationMessages.EMAIL_VERIFICATION_SUBJECT;
import static com.oxygensend.auth.domain.NotificationMessages.PASSWORD_RESET_MESSAGE;
import static com.oxygensend.auth.domain.NotificationMessages.PASSWORD_RESET_SUBJECT;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final NotificationRepository notificationRepository;
    private final JwtFacade jwtFacade;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;

    public void delete(UUID userId) {
        if (!repository.existsById(userId)) {
            throw new UserNotFoundException("User with %s not found".formatted(userId));
        }
        repository.deleteById(userId);
    }

    public void block(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        repository.save(user.blocked());
    }

    public void unblock(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> new UserNotFoundException("User with %s not found".formatted(userId)));

        repository.save(user.unblocked());
    }

    public void generatePasswordResetToken(UUID userId) {
        generateAndSendToken(userId, TokenType.PASSWORD_RESET, PASSWORD_RESET_SUBJECT, PASSWORD_RESET_MESSAGE);
    }

    public void resetPassword(PasswordResetRequest request) {
        var token = (PasswordResetTokenPayload) jwtFacade.validateToken(request.token(), TokenType.PASSWORD_RESET);
        var user = repository.findById(UUID.fromString(token.userId()))
                             .orElseThrow(() -> new MissingUserException("User with id %s not found".formatted(token.userId())));

        var newPassword = passwordEncoder.encode(request.newPassword());
        repository.save(user.withNewPassword(newPassword));
    }

    public void changePassword(PasswordChangeRequest request) {
        var user = authenticationFacade.getAuthenticationPrinciple();

        if (!passwordEncoder.matches(request.oldPassword(), user.password())) {
            throw new PasswordMismatchException("Old password does not match");
        }

        var newPassword = passwordEncoder.encode(request.newPassword());
        repository.save(user.withNewPassword(newPassword));
    }

    public void generateEmailVerificationToken(UUID userId) {
        generateAndSendToken(userId, TokenType.EMAIL_VERIFICATION, EMAIL_VERIFICATION_SUBJECT, EMAIL_VERIFICATION_MESSAGE);
    }


    private void generateAndSendToken(UUID userId, TokenType tokenType, String subject, String message) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        var token = jwtFacade.generateToken(user, tokenType);
        var command = new SendMailCommand(user, subject, message.formatted(user.fullName(), token));
        notificationRepository.sendMail(command);
    }


    public void verifyEmail(VerifyEmailRequest request) {
        var token = (EmailVerificationTokenPayload) jwtFacade.validateToken(request.token(), TokenType.EMAIL_VERIFICATION);
        var user = repository.findById(UUID.fromString(token.userId()))
                             .orElseThrow(() -> new MissingUserException("User with id %s not found".formatted(token.userId())));

        repository.save(user.withEmailVerified());
    }
}
