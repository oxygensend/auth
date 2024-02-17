package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.DataSourceObjectAdapter;
import com.oxygensend.auth.domain.Session;
import org.springframework.stereotype.Component;

@Component
final class SessionMongoAdapter implements DataSourceObjectAdapter<Session, SessionMongo> {
    @Override
    public Session toDomain(SessionMongo sessionMongo) {
        return new Session(sessionMongo.id());
    }

    @Override
    public SessionMongo toDataSource(Session session) {
        return new SessionMongo(session.id());
    }
}
