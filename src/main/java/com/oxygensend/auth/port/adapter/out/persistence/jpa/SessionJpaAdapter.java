//package com.oxygensend.auth.infrastructure.jpa;
//
//import com.oxygensend.auth.infrastructure.mongo.DataSourceObjectAdapter;
//import com.oxygensend.auth.domain.auth.model.Session;
//import org.springframework.stereotype.Component;
//
//@Component
//final class SessionJpaAdapter implements DataSourceObjectAdapter<Session, SessionJpa> {
//    @Override
//    public Session toDomain(SessionJpa sessionJpa) {
//        return new Session(sessionJpa.id());
//    }
//
//    @Override
//    public SessionJpa toDataSource(Session session) {
//        return new SessionJpa(session.id());
//    }
//}
