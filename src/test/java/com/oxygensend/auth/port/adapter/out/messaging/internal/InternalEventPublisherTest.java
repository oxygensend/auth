package com.oxygensend.auth.port.adapter.out.messaging.internal;

import static org.mockito.Mockito.verify;

import com.oxygensend.auth.port.adapter.out.messaging.TestDomainEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private InternalEventPublisher publisher;


    @Test
    void shouldPublishEvent() {
        // Given
        TestDomainEvent event = new TestDomainEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            "test-data"
        );

        // When
        publisher.publish(event);

        // Then
        verify(applicationEventPublisher).publishEvent(event);
    }
}
