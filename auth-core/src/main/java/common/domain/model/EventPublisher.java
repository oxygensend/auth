package common.domain.model;

public interface EventPublisher {

    void publish(DomainEvent event);

}
