package common.domain.model;

import com.oxygensend.auth.domain.model.identity.User;

import java.time.Instant;

public interface DomainEvent {

    default String name() {
        return getClass().getSimpleName();
    }

    String key();

    default String aggregateType() {
        return User.class.getSimpleName();
    }

    default Instant occurredOn() {
        return Instant.now();
    }
}
