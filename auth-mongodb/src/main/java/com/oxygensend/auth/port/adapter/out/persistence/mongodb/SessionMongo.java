package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("sessions")
record SessionMongo(@Id UUID id,
                    UUID userId) {
}
