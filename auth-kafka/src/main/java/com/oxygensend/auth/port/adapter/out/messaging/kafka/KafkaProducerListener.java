package com.oxygensend.auth.port.adapter.out.messaging.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

class KafkaProducerListener<K, V> implements ProducerListener<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerListener.class);

    @Override
    public void onSuccess(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata) {
        LOGGER.info("Event with key: {} transferred successfully to topic: {}", producerRecord.key(),
                    producerRecord.topic());
    }

    @Override
    public void onError(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        LOGGER.info("Event with key: {} wasn't transferred to topic: {}", producerRecord.key(), producerRecord.topic());
        throw new RuntimeException(
            "Message sending failure %s %s".formatted(producerRecord.key(), producerRecord.topic()));
    }
}
