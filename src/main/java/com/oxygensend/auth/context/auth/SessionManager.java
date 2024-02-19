package com.oxygensend.auth.context.auth;


import com.oxygensend.auth.context.auth.jwt.JwtFacade;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.domain.Session;
import com.oxygensend.auth.domain.SessionRepository;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import com.oxygensend.auth.domain.exception.SessionExpiredException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRepository sessionRepository;
    private final JwtFacade jwtFacade;

    public void startSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
        sessionRepository.save(new Session(sessionId));
    }

    public Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(SessionExpiredException::new);
    }

    public AuthenticationResponse prepareSession(User user) {
        // Generate refresh token
        String refreshToken = jwtFacade.generateToken(user, TokenType.REFRESH);

        // Start session for this user
        startSession(user.id());

        // Generate access token
        var accessToken = jwtFacade.generateToken(user, TokenType.ACCESS);
        return new AuthenticationResponse(accessToken, refreshToken);

    }
}
