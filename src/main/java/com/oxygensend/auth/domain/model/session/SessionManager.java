package com.oxygensend.auth.domain.model.session;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.exception.SessionExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final SessionRepository sessionRepository;

    public SessionId startSession(UserId userId) {
        // Todo - sessionid generation can be leave to the repository
        var sessionId = sessionRepository.nextIdentity();
        sessionRepository.removeByUserId(userId);
        sessionRepository.save(new Session(sessionId, userId));
        return sessionId;
    }

    public Session currentSession(UserId userId) {
        return sessionRepository.sessionOfUserId(userId).orElseThrow(SessionExpiredException::new);
    }

}
