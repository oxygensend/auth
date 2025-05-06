package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.port.Ports;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Profile(Ports.MONGODB)
@Document("domain_events")
public record DomainEventMongo(ObjectId id,
                               String eventId,
                               String aggregateType,
                               String eventType,
                               String eventPayload,
                               Instant occurredOn) {
}
