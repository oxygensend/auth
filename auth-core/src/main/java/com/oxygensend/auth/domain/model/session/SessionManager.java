package com.oxygensend.auth.domain.model.session;

import com.oxygensend.auth.domain.model.identity.UserId;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private final SessionRepository sessionRepository;

    public SessionManager(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

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
