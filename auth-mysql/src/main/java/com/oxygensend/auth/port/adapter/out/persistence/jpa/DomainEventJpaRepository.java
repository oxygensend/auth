package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import com.oxygensend.common.domain.model.DomainEvent;
import com.oxygensend.common.domain.model.EventPublisher;

@Repository
public class DomainEventJpaRepository {

    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    public DomainEventJpaRepository(EventPublisher eventPublisher, ObjectMapper objectMapper,
                                    EntityManager entityManager) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
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
