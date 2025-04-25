package com.oxygensend.auth.application.auth;

import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.domain.event.EventPublisher;
import com.oxygensend.auth.domain.event.EventWrapper;
import com.oxygensend.auth.domain.model.identity.AuthenticationService;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.RegistrationService;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserDescriptor;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;
import com.oxygensend.auth.domain.model.identity.event.RegisterEvent;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionManager;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.RefreshTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenException;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;


@Service
public class AuthService {

    private final TokenApplicationService tokenApplicationService;
    private final SettingsProperties.SignInProperties signInProperties;
    private final EventPublisher eventPublisher;
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final PasswordService passwordService;
    private final LoginProvider loginProvider;
    private final SessionManager sessionManager;

    public AuthService(TokenApplicationService tokenApplicationService, SettingsProperties signInProperties,
                       EventPublisher eventPublisher, AuthenticationService authenticationService,
                       RegistrationService registrationService, PasswordService passwordService,
                       LoginProvider loginProvider, SessionManager sessionManager) {
        this.tokenApplicationService = tokenApplicationService;
        this.signInProperties = signInProperties.signIn();
        this.eventPublisher = eventPublisher;
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
        this.passwordService = passwordService;
        this.loginProvider = loginProvider;
        this.sessionManager = sessionManager;
    }


    @Transactional
    public Map.Entry<UserId, AuthenticationTokensDto> register(RegisterCommand command) {
        var password = Password.fromPlaintext(command.rawPassword(), passwordService);
        var credentials = new Credentials(command.email(), command.userName(), password);

        var user = registrationService.registerUser(credentials,
                                                    command.roles(),
                                                    command.businessId(),
                                                    signInProperties.accountActivation());

        String refreshToken =
            tokenApplicationService.createToken(new RefreshTokenSubject(user.id()), TokenType.REFRESH);

        publishRegisterEvent(user);

        // Generate access token
        var accessToken = tokenApplicationService.createToken(new AccessTokenSubject(user.id(),
                                                                                     user.businessId(),
                                                                                     user.roles(),
                                                                                     user.isVerified(),
                                                                                     user.userName(),
                                                                                     user.email()), TokenType.ACCESS);
        return Map.entry(user.id(), new AuthenticationTokensDto(accessToken, refreshToken));
    }

    @Transactional
    public AuthenticationTokensDto authenticate(String login, String password) {
        UserDescriptor userDescriptor = authenticateUser(loginProvider.get(login), password);
        sessionManager.startSession(userDescriptor.userId());

        return generateTokens(userDescriptor);
    }

    @Transactional
    public AuthenticationTokensDto refreshToken(String token) {
        var payload = getRefreshTokenPayload(token);
        Session session = sessionManager.currentSession(payload.userId());
        UserDescriptor userDescriptor = authenticationService.revalidateUser(session.userId());
        return generateTokens(userDescriptor);
    }

    private UserDescriptor authenticateUser(LoginDto login, String password) {
        return switch (login.type()) {
            case USERNAME -> authenticationService.authenticateWithUsername(new UserName(login), password);
            case EMAIL -> authenticationService.authenticateWithEmail(new EmailAddress(login), password);
        };
    }

    private AuthenticationTokensDto generateTokens(UserDescriptor userDescriptor) {
        // Generate refresh token
        String refreshToken = tokenApplicationService.createToken(new RefreshTokenSubject(userDescriptor.userId()),
                                                                  TokenType.REFRESH);

        // Generate access token
        var accessToken = tokenApplicationService.createToken(new AccessTokenSubject(userDescriptor.userId(),
                                                                                     userDescriptor.businessId(),
                                                                                     userDescriptor.roles(),
                                                                                     userDescriptor.verified(),
                                                                                     userDescriptor.userName(),
                                                                                     userDescriptor.email()),
                                                              TokenType.ACCESS);
        return new AuthenticationTokensDto(accessToken, refreshToken);
    }

    private RefreshTokenPayload getRefreshTokenPayload(String token) {
        var payload = (RefreshTokenPayload) tokenApplicationService.parseToken(token, TokenType.REFRESH);
        if (payload.exp().before(new Date())) {
            throw new TokenException("Token expired");
        }
        return payload;
    }

    private void publishRegisterEvent(User user) {
        var event =
            new RegisterEvent(user.id().value(), user.businessId().value(), user.credentials().email().address(),
                              signInProperties.accountActivation());
        eventPublisher.publish(new EventWrapper(event, signInProperties.registerEventTopic()));
    }
}
