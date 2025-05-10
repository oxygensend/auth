package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.common.domain.model.EventPublisher;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DomainEventJpaRepositoryTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DomainEventJpaRepository repository;


    @Test
    @DisplayName("Given domain events when saveAndPublish is called then events should be persisted and published")
    void saveAndPublish_shouldPersistAndPublishEvents() throws JsonProcessingException {
        // Given
        TestDomainEvent event = new TestDomainEvent();
        String eventJson = "{\"someField\":\"someValue\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(eventJson);

        // When
        repository.saveAndPublish(List.of(event));

        // Then
        ArgumentCaptor<DomainEventJpa> jpaEventCaptor = ArgumentCaptor.forClass(DomainEventJpa.class);
        verify(entityManager).persist(jpaEventCaptor.capture());
        verify(eventPublisher).publish(event);

        DomainEventJpa capturedEvent = jpaEventCaptor.getValue();
        assertThat(capturedEvent.eventId).isEqualTo(event.id());
        assertThat(capturedEvent.aggregateType).isEqualTo(event.aggregateType());
        assertThat(capturedEvent.eventType).isEqualTo(TestDomainEvent.class.getName());
        assertThat(capturedEvent.eventPayload).isEqualTo(eventJson);
        assertThat(capturedEvent.occurredOn).isEqualTo(event.occurredOn());
    }

    @Test
    @DisplayName("Given empty event list when saveAndPublish is called then nothing should happen")
    void saveAndPublish_givenEmptyList_shouldDoNothing() {
        // When
        repository.saveAndPublish(Collections.emptyList());

        // Then
        verifyNoInteractions(entityManager, eventPublisher, objectMapper);
    }

    @Test
    @DisplayName("Given JSON processing exception when saveAndPublish is called then it should throw RuntimeException")
    void saveAndPublish_givenJsonProcessingException_shouldThrowRuntimeException() throws JsonProcessingException {
        // Given
        TestDomainEvent event = new TestDomainEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Failed to serialize") {
        });

        // When/Then
        assertThatThrownBy(() -> repository.saveAndPublish(List.of(event)))
            .isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(JsonProcessingException.class);

        verify(entityManager, never()).persist(any());
        verify(eventPublisher, never()).publish(any());
    }


}
