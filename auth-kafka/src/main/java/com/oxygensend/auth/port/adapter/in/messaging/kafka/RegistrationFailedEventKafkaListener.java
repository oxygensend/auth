package com.oxygensend.auth.port.adapter.in.messaging.kafka;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.port.adapter.in.messaging.RegistrationFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationFailedEventKafkaListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationFailedEventKafkaListener.class);
    private final UserService userService;

    public RegistrationFailedEventKafkaListener(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(id = "registrationFailed", topics = "${kafka.consumer.topic.registration-failed-topic}",
        containerFactory = "registrationFailedEventConcurrentKafkaListenerContainerFactory")
    public void handleEvent(RegistrationFailedEvent event) {
        LOGGER.warn("Registration failed for user {}: {} at {}", event.userId(), event.reason(), event.occurredOn());

        userService.delete(event.userId());
    }
}
