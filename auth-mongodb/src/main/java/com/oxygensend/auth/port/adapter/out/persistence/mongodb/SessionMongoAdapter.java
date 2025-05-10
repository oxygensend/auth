package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.stereotype.Component;

@Component
final class SessionMongoAdapter implements DataSourceObjectAdapter<Session, SessionMongo> {
    @Override
    public Session toDomain(SessionMongo sessionMongo) {
        return new Session(new SessionId(sessionMongo.id()), new UserId(sessionMongo.userId()));
    }

    @Override
    public SessionMongo toDataSource(Session session) {
        return new SessionMongo(session.id().value(), session.userId().value());
    }
}
