package com.oxygensend.auth.infrastructure.mongo;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sessions")
record SessionMongo(@Id UUID id) {
}
