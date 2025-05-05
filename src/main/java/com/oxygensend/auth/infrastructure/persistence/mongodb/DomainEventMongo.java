package com.oxygensend.auth.infrastructure.persistence.mongodb;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Profile("MONGODB")
@Document("domain_events")
public record DomainEventMongo(ObjectId id,
                               String eventId,
                               String aggregateType,
                               String eventType,
                               String eventPayload,
                               Instant occurredOn) {
}
