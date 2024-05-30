package com.oxygensend.auth.context.user;

import com.oxygensend.auth.config.IdentityType;
import com.oxygensend.auth.context.IdentityProvider;
import com.oxygensend.auth.context.auth.AuthenticationFacade;
import com.oxygensend.auth.context.auth.request.PasswordChangeRequest;
import com.oxygensend.auth.context.auth.request.PasswordResetRequest;
import com.oxygensend.auth.context.auth.request.VerifyEmailRequest;
import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.context.jwt.payload.EmailVerificationTokenPayload;
import com.oxygensend.auth.context.jwt.payload.PasswordResetTokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.exception.MissingUserException;
import com.oxygensend.auth.domain.exception.PasswordMismatchException;
import com.oxygensend.auth.domain.exception.UserAlreadyExistsException;
import com.oxygensend.auth.domain.exception.UserNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JwtFacade jwtFacade;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;
    private final IdentityProvider identityProvider;

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

    public String generatePasswordResetToken(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return jwtFacade.generateToken(user, TokenType.PASSWORD_RESET);
    }

    public void resetPassword(PasswordResetRequest request) {
        var token = (PasswordResetTokenPayload) jwtFacade.validateToken(request.token(), TokenType.PASSWORD_RESET);
        var user = repository.findById(UUID.fromString(token.userId()))
                             .orElseThrow(() -> new MissingUserException("User with id %s not found".formatted(token.userId())));

        var newPassword = passwordEncoder.encode(request.newPassword());
        repository.save(user.withPasswordReset(newPassword));
    }

    public void changePassword(PasswordChangeRequest request) {
        var user = authenticationFacade.getAuthenticationPrinciple();

        if (!passwordEncoder.matches(request.oldPassword(), user.password())) {
            throw new PasswordMismatchException("Old password does not match");
        }

        var newPassword = passwordEncoder.encode(request.newPassword());
        repository.save(user.withNewPassword(newPassword));
    }

    public String generateEmailVerificationToken(UUID userId) {
        var user = repository.findById(userId)
                             .orElseThrow(() -> UserNotFoundException.withId(userId));

        return jwtFacade.generateToken(user, TokenType.EMAIL_VERIFICATION);
    }

    public void verifyEmail(VerifyEmailRequest request) {
        var token = (EmailVerificationTokenPayload) jwtFacade.validateToken(request.token(), TokenType.EMAIL_VERIFICATION);
        var user = repository.findById(UUID.fromString(token.userId()))
                             .orElseThrow(() -> new MissingUserException("User with id %s not found".formatted(token.userId())));

        repository.save(user.withEmailVerified());
    }

    public void createUser(CreateUserRequest request) {
        repository.findByUsername(request.identity()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var email = identityProvider.getIdentityType() == IdentityType.EMAIL ? request.identity() : null;
        var user = User.builder()
                       .id(request.id())
                       .businessId(request.businessId())
                       .email(email)
                       .password(passwordEncoder.encode(request.password()))
                       .roles(request.roles())
                       .verified(request.verified())
                       .build();

        repository.save(user);
    }
}
