package com.oxygensend.auth.port.adapter.in.messaging.kafka;

import static org.mockito.Mockito.verify;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.port.adapter.in.messaging.RegistrationFailedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class RegistrationFailedEventListenerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationFailedEventKafkaListener listener;

    @Test
    void shouldDeleteUser_whenRegistrationFailedEventIsReceived() {
        // Given
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);
        String reason = "Email verification failed";

        RegistrationFailedEvent event = new RegistrationFailedEvent(
            userId,
            reason,
            Instant.now()
        );

        // When
        listener.handleEvent(event);

        // Then
        verify(userService).delete(userId);
    }
}
