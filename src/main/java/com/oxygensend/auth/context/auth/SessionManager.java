package com.oxygensend.auth.context.auth;


import com.oxygensend.auth.config.TokenProperties;
import com.oxygensend.auth.context.auth.jwt.TokenStorage;
import com.oxygensend.auth.context.auth.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRepository sessionRepository;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final TokenProperties tokenProperties;
    private final TokenStorage tokenStorage;

    public void startSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
        sessionRepository.save(new Session(sessionId));
    }

    public Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(SessionExpiredException::new);
    }

    public AuthenticationResponse prepareSession(User user) {

        // Generate refresh token
        var refreshPayload = tokenPayloadFactory.createToken(
                TokenType.REFRESH,
                new Date(System.currentTimeMillis() + tokenProperties.authExpirationMs()),
                new Date(System.currentTimeMillis()),
                user
        );
        String refreshToken = tokenStorage.generateToken(refreshPayload);

        // Start session for this user
        startSession(user.id());

        // Generate access token
        var accessPayload = tokenPayloadFactory.createToken(
                TokenType.ACCESS,
                new Date(System.currentTimeMillis() + tokenProperties.authExpirationMs()),
                new Date(System.currentTimeMillis()),
                user
        );

        String accessToken = tokenStorage.generateToken(accessPayload);
        return new AuthenticationResponse(accessToken, refreshToken);

    }
}
