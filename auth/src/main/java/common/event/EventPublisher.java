package common.event;

import common.domain.model.DomainEvent;

public interface EventPublisher {

    void publish(DomainEvent event);

}
