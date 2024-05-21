package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.config.IdentityType;
import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.context.IdentityProvider;
import com.oxygensend.auth.context.auth.request.AuthenticationRequest;
import com.oxygensend.auth.context.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.context.auth.request.RegisterRequest;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.context.auth.response.RegisterResponse;
import com.oxygensend.auth.context.auth.response.ValidationResponse;
import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.context.jwt.payload.RefreshTokenPayload;
import com.oxygensend.auth.context.user.UserIdProvider;
import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.event.EventPublisher;
import com.oxygensend.auth.domain.event.EventWrapper;
import com.oxygensend.auth.domain.event.RegisterEvent;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import com.oxygensend.auth.domain.exception.TokenException;
import com.oxygensend.auth.domain.exception.UnauthorizedException;
import com.oxygensend.auth.domain.exception.UserAlreadyExistsException;
import java.util.Date;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtFacade jwtFacade;
    private final SettingsProperties.SignInProperties signInProperties;
    private final EventPublisher eventPublisher;
    private final IdentityProvider identityProvider;
    private final UserIdProvider userIdProvider;

    public AuthService(SessionManager sessionManager, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtFacade jwtFacade, SettingsProperties settingsProperties,
                       EventPublisher eventPublisher, IdentityProvider identityProvider, UserIdProvider userIdProvider) {
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtFacade = jwtFacade;
        this.signInProperties = settingsProperties.signIn();
        this.eventPublisher = eventPublisher;
        this.identityProvider = identityProvider;
        this.userIdProvider = userIdProvider;
    }

    public RegisterResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.identity()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });

        var verified = signInProperties.accountActivation() == AccountActivation.NONE;
        var username = identityProvider.getIdentityType() == IdentityType.USERNAME ? request.identity() : null;
        var email = identityProvider.getIdentityType() == IdentityType.EMAIL ? request.identity() : null;

        var user = User.builder()
                       .id(userIdProvider.get())
                       .username(username)
                       .email(email)
                       .password(passwordEncoder.encode(request.password()))
                       .roles(request.roles())
                       .locked(false)
                       .verified(verified)
                       .businessId(request.businessId())
                       .build();

        userRepository.save(user);

        publishRegisterEvent(user);

        var tokens = sessionManager.prepareSession(user);

        return new RegisterResponse(user.id(), tokens.accessToken(), tokens.refreshToken());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.identity(),
                            request.password()
                    )
            );
            return sessionManager.prepareSession((User) authentication.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        var payload = getRefreshTokenPayload(request.token());
        var session = sessionManager.getSession(payload.sessionId());
        var user = userRepository.findById(session.id())
                                 .orElseThrow(() -> new SessionExpiredException("User not found by session id"));

        return sessionManager.prepareSession(user);
    }

    public ValidationResponse validateToken(String userId, List<GrantedAuthority> authorities) {
        return new ValidationResponse(userId, authorities);
    }

    private RefreshTokenPayload getRefreshTokenPayload(String token) {
        var payload = (RefreshTokenPayload) jwtFacade.validateToken(token, TokenType.REFRESH);
        if (payload.exp().before(new Date())) {
            throw new TokenException("Token expired");
        }
        return payload;
    }

    private void publishRegisterEvent(User user) {
        var event = new RegisterEvent(user.id(), user.businessId(), user.email(), signInProperties.accountActivation());
        eventPublisher.publish(new EventWrapper(event, signInProperties.registerEventTopic()));
    }
}
