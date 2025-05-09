package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import common.domain.model.DomainEvent;
import common.domain.model.EventPublisher;

@Repository
public class DomainEventJpaRepository {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    @PersistenceContext
    private EntityManager entityManager;

    public DomainEventJpaRepository(EventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    public void saveAndPublish(Collection<DomainEvent> events) {
        for (DomainEvent event : events) {
            try {
                var jpaEvent = new DomainEventJpa(
                    event.id(),
                    event.aggregateType(),
                    event.getClass().getName(),
                    objectMapper.writeValueAsString(event),
                    event.occurredOn()
                );

                entityManager.persist(jpaEvent);
                eventPublisher.publish(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
