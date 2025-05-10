package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("domain_events")
public record DomainEventMongo(ObjectId id,
                               String eventId,
                               String aggregateType,
                               String eventType,
                               String eventPayload,
                               Instant occurredOn) {
}
