package com.oxygensend.auth.port.adapter.out.messaging.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@ExtendWith(MockitoExtension.class)
class KafkaProducerListenerTest {

    @InjectMocks
    private KafkaProducerListener<String, String> listener;

    private ProducerRecord<String, String> record;
    private RecordMetadata metadata;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // Set up record and metadata
        String topic = "test-topic";
        String key = "test-key";
        String value = "test-value";

        record = new ProducerRecord<>(topic, key, value);
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        metadata = new RecordMetadata(topicPartition, 0L, 0L, 0L, 0L, 0, 0);

        // Set up logger
        Logger logger = (Logger) LoggerFactory.getLogger(KafkaProducerListener.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void shouldHandleSuccessfulProducing() {
        // When
        listener.onSuccess(record, metadata);

        // Then
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage)
            .anyMatch(
                message -> message.equals("Event with key: test-key transferred successfully to topic: test-topic"));

        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getLevel)
            .contains(Level.INFO);
    }

    @Test
    void shouldThrowExceptionOnError() {
        // Given
        Exception exception = new RuntimeException("Test exception");

        // When/Then
        assertThatThrownBy(() -> listener.onError(record, metadata, exception))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("test-key")
            .hasMessageContaining("test-topic");

        // Verify logs
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage)
            .anyMatch(message -> message.equals("Event with key: test-key wasn't transferred to topic: test-topic"));

        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getLevel)
            .contains(Level.INFO);
    }
}
