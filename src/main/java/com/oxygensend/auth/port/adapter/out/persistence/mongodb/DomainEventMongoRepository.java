package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.auth.port.Ports;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;

import common.domain.model.DomainEvent;
import common.domain.model.EventPublisher;

@Profile(Ports.MONGODB)
@Configuration
public class DomainEventMongoRepository {

    private final MongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public DomainEventMongoRepository(MongoTemplate mongoTemplate, EventPublisher eventPublisher,
                                      ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
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
