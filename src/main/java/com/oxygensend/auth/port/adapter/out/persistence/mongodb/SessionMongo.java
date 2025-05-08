package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.port.Ports;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile(Ports.MONGODB)
@Document("sessions")
record SessionMongo(@Id UUID id,
                    UUID userId) {
}
