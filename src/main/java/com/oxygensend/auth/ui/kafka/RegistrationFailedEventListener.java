package com.oxygensend.auth.ui.kafka;

import com.oxygensend.auth.application.identity.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationFailedEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationFailedEventListener.class);
    private final UserService userService;

    public RegistrationFailedEventListener(UserService userService) {
        this.userService = userService;
    }

    public void handleEvent(RegistrationFailedEvent event) {
        LOGGER.error("Registration failed for user {}: {}", event.userId(), event.reason());

        userService.delete(event.userId());
    }
}
