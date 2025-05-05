package com.oxygensend.auth.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;

import common.domain.model.DomainEvent;
import common.event.EventPublisher;

@Configuration
public class DomainEventMongoRepository {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;

    public DomainEventMongoRepository(MongoTemplate mongoTemplate, EventPublisher eventPublisher) {
        this.mongoTemplate = mongoTemplate;
        this.eventPublisher = eventPublisher;
    }

    void saveAndPublish(Collection<DomainEvent> events) {
        for (DomainEvent event : events) {
            try {
                var mongoEvent = new DomainEventMongo(null,
                                                      event.id(),
                                                      event.aggregateType(),
                                                      event.getClass().getName(),
                                                      objectMapper.writeValueAsString(event),
                                                      event.occurredOn());

                mongoTemplate.save(mongoEvent, "domain_events");
                eventPublisher.publish(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
