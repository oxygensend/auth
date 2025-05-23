package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.common.domain.model.DomainEvent;
import com.oxygensend.common.domain.model.EventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
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
