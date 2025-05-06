package com.oxygensend.auth.port.adapter.in.messaging;

import com.oxygensend.auth.domain.model.identity.UserId;
import org.springframework.context.annotation.Profile;

@Profile("KAFKA")
public record RegistrationFailedEvent(UserId userId, String reason) {
}
