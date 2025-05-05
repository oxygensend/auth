package com.oxygensend.auth.infrastructure.persistence;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile("mongo")
@Document("sessions")
record SessionMongo(@Id UUID id,
                    UUID userId) {
}
