package com.oxygensend.auth.context.auth;

import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.context.jwt.JwtFacade;
import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.jwt.payload.RefreshTokenPayload;
import com.oxygensend.auth.context.auth.request.AuthenticationRequest;
import com.oxygensend.auth.context.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.context.auth.request.RegisterRequest;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.context.auth.response.ValidationResponse;
import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.UserRepository;
import com.oxygensend.auth.domain.event.EventPublisher;
import com.oxygensend.auth.domain.event.RegisterEvent;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import com.oxygensend.auth.domain.exception.TokenException;
import com.oxygensend.auth.domain.exception.UnauthorizedException;
import com.oxygensend.auth.domain.exception.UserAlreadyExistsException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

    public AuthService(SessionManager sessionManager, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtFacade jwtFacade, SettingsProperties settingsProperties,
                       EventPublisher eventPublisher) {
        this.sessionManager = sessionManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtFacade = jwtFacade;
        this.signInProperties = settingsProperties.signIn();
        this.eventPublisher = eventPublisher;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new UserAlreadyExistsException();
        });
        var enabled = signInProperties.accountActivation() != AccountActivation.NONE;

        var user = User.builder()
                       .id(UUID.randomUUID())
                       .email(request.email())
                       .firstName(request.firstName())
                       .lastName(request.lastName())
                       .password(passwordEncoder.encode(request.password()))
                       .enabled(enabled)
                       .locked(false)
                       .roles(request.roles())
                       .build();

        userRepository.save(user);
        eventPublisher.publish(new RegisterEvent(user.id(), user.email(), user.createdAt(), signInProperties.accountActivation()));

        return sessionManager.prepareSession(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
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

    private RefreshTokenPayload getRefreshTokenPayload(String token) {
        var payload = (RefreshTokenPayload) jwtFacade.validateToken(token, TokenType.REFRESH);
        if (payload.exp().before(new Date())) {
            throw new TokenException("Token expired");
        }
        return payload;
    }

    private AccessTokenPayload getAccessTokenPayload(String token) {
        var payload = (AccessTokenPayload) jwtFacade.validateToken(token, TokenType.ACCESS);
        if (payload.exp().before(new Date())) {
            throw new TokenException("Token expired");
        }
        return payload;
    }

    public ValidationResponse validateToken(UUID userId, List<GrantedAuthority> authorities) {
        boolean isAuthorized = userId != null && authorities != null;
        return new ValidationResponse(isAuthorized, userId, authorities);
    }

}
