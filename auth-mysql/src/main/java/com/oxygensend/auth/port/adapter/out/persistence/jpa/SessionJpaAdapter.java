package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
final class SessionJpaAdapter implements DataSourceObjectAdapter<Session, SessionJpa> {
    @Override
    public Session toDomain(SessionJpa sessionJpa) {
        return new Session(new SessionId(sessionJpa.id), new UserId(sessionJpa.userId));
    }

    @Override
    public SessionJpa toDataSource(Session session) {
        return new SessionJpa(session.id().value(), session.userId().value());
    }
}
