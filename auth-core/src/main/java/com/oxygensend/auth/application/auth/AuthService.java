package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.application.settings.CurrentAccountActivationType;
import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginProvider;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.identity.AuthenticationService;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserDescriptor;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionManager;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.RefreshTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final TokenApplicationService tokenApplicationService;
    private final AuthenticationService authenticationService;
    private final LoginProvider loginProvider;
    private final SessionManager sessionManager;
    private final CurrentAccountActivationType currentAccountActivationType;
    private final UserService userService;

    public AuthService(TokenApplicationService tokenApplicationService, AuthenticationService authenticationService,
                       LoginProvider loginProvider, SessionManager sessionManager,
                       CurrentAccountActivationType currentAccountActivationType, UserService userService) {
        this.tokenApplicationService = tokenApplicationService;
        this.authenticationService = authenticationService;
        this.loginProvider = loginProvider;
        this.sessionManager = sessionManager;
        this.currentAccountActivationType = currentAccountActivationType;
        this.userService = userService;
    }


    @Transactional
    public Pair<UserId, AuthenticationTokensDto> register(RegisterCommand command) {
        LOGGER.info("Registering user with email: {}", command.email());
        var user = userService.registerUser(command.toRegisterUserCommand(currentAccountActivationType.get()));
        sessionManager.startSession(user.id());

        var authenticationTokensDto = generateTokens(user.id(),
                                                     user.businessId(),
                                                     user.username(),
                                                     user.email(),
                                                     user.roles(),
                                                     user.isVerified());
        LOGGER.info("User registered with email: {}", command.email());
        return Pair.of(user.id(), authenticationTokensDto);
    }

    @Transactional
    public AuthenticationTokensDto authenticate(User user) {
        sessionManager.startSession(user.id());
        return generateTokens(user.id(),
                              user.businessId(),
                              user.username(),
                              user.email(),
                              user.roles(),
                              user.isVerified());
    }
    @Transactional
    public AuthenticationTokensDto authenticate(String login, String password) {
        UserDescriptor userDescriptor = authenticateUser(loginProvider.get(login), password);
        sessionManager.startSession(userDescriptor.userId());

        return generateTokens(userDescriptor.userId(),
                              userDescriptor.businessId(),
                              userDescriptor.username(),
                              userDescriptor.email(),
                              userDescriptor.roles(),
                              userDescriptor.verified());
    }

    @Transactional
    public AuthenticationTokensDto refreshToken(String token) {
        var payload = getRefreshTokenPayload(token);
        Session session = sessionManager.currentSession(payload.userId());
        UserDescriptor userDescriptor = authenticationService.revalidateUser(session.userId());
        return generateTokens(userDescriptor.userId(),
                              userDescriptor.businessId(),
                              userDescriptor.username(),
                              userDescriptor.email(),
                              userDescriptor.roles(),
                              userDescriptor.verified());
    }

    private UserDescriptor authenticateUser(LoginDto login, String password) {
        return switch (login.type()) {
            case USERNAME -> authenticationService.authenticateWithUsername(new Username(login), password);
            case EMAIL -> authenticationService.authenticateWithEmail(new EmailAddress(login), password);
        };
    }

    private AuthenticationTokensDto generateTokens(UserId userId, BusinessId businessId,
                                                   Username userName, EmailAddress email,
                                                   Set<Role> roles, boolean verified) {
        // Generate refresh token
        String refreshToken = tokenApplicationService.createToken(new RefreshTokenSubject(userId), TokenType.REFRESH);

        // Generate access token
        var accessToken = tokenApplicationService.createToken(new AccessTokenSubject(userId,
                                                                                     businessId,
                                                                                     roles,
                                                                                     verified,
                                                                                     userName,
                                                                                     email),
                                                              TokenType.ACCESS);
        return new AuthenticationTokensDto(accessToken, refreshToken);
    }

    private RefreshTokenPayload getRefreshTokenPayload(String token) {
        var payload = (RefreshTokenPayload) tokenApplicationService.parseToken(token, TokenType.REFRESH);
        if (payload.isExpired()) {
            throw new ExpiredRefreshTokenException();
        }
        return payload;
    }

}
