package com.oxygensend.auth.port.adapter.in.messaging.kafka;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.port.adapter.in.messaging.RegistrationFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Profile("KAFKA")
@Component
public class RegistrationFailedEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationFailedEventListener.class);
    private final UserService userService;

    public RegistrationFailedEventListener(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(id = "registrationFailed", topics = "${kafka.consumer.topic.registration-failed-topic}",
        containerFactory = "registrationFailedEventConcurrentKafkaListenerContainerFactory")
    public void handleEvent(RegistrationFailedEvent event) {
        LOGGER.warn("Registration failed for user {}: {}", event.userId(), event.reason());

        userService.delete(event.userId());
    }
}
