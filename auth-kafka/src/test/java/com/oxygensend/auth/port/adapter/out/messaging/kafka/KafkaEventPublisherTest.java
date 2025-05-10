package com.oxygensend.auth.port.adapter.out.messaging.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.time.Instant;
import java.util.UUID;

import com.oxygensend.common.domain.model.DomainEvent;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @InjectMocks
    private KafkaEventPublisher publisher;

    @Captor
    private ArgumentCaptor<Message<DomainEvent>> messageCaptor;

    @Test
    void shouldPublishEventWithCorrectHeaders() {
        // Given
        String eventId = UUID.randomUUID().toString();
        String aggregateType = "TestAggregate";
        TestDomainEvent event = new TestDomainEvent(
            eventId,
            Instant.now(),
            aggregateType,
            "test-data"
        );

        // When
        publisher.publish(event);

        // Then
        verify(kafkaTemplate).send(messageCaptor.capture());

        Message<DomainEvent> capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getPayload()).isEqualTo(event);
        assertThat(capturedMessage.getHeaders().get("kafka_messageKey")).isEqualTo(eventId);
        assertThat(capturedMessage.getHeaders().get("aggregateType")).isEqualTo(aggregateType);
        assertThat(capturedMessage.getHeaders().get("eventType")).isEqualTo("TestDomainEvent");
    }
}
