package com.oxygensend.auth.domain.event;

public interface EventPublisher {

    void publish(EventWrapper eventWrapper);

}
