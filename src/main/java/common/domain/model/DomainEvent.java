package common.domain.model;

import com.oxygensend.auth.domain.model.identity.User;

import java.time.Instant;

public interface DomainEvent {

    String id();

    default String aggregateType() {
        return User.class.getSimpleName();
    }

    default Instant occurredOn() {
        return Instant.now();
    }
}
