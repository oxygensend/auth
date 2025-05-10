package com.oxygensend.auth.port.adapter.out.messaging.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.port.adapter.out.messaging.TestDomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalDomainEventListenerTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    @InjectMocks
    private InternalDomainEventListener listener;


    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void shouldHandleEvent() {
        // Given
        TestDomainEvent event = new TestDomainEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            "test-data"
        );

        // When
        listener.handleEvent(event);

        // Then
        assertThat(outputStreamCaptor.toString()).contains("Handling event: " + event.toString());
    }
}
