package com.oxygensend.common.domain.model;

public interface EventPublisher {

    void publish(DomainEvent event);

}
