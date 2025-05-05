package com.oxygensend.auth.ui.kafka;

import com.oxygensend.auth.domain.model.identity.UserId;

public record RegistrationFailedEvent(UserId userId, String reason) {
}
