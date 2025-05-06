package com.oxygensend.auth.port.adapter.in.messaging;

import com.oxygensend.auth.domain.model.identity.UserId;

import java.time.Instant;

public record RegistrationFailedEvent(UserId userId, String reason, Instant occurredAt) {

}
