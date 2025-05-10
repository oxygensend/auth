package com.oxygensend.auth.port.adapter.out.persistence.mongodb;//package com.oxygensend.auth.port.adapter.out.persistence.mongodb;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.oxygensend.auth.port.adapter.out.messaging.TestDomainEvent;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//import com.oxygensend.common.domain.model.DomainEvent;
//import com.oxygensend.common.domain.model.EventPublisher;
//
//@ExtendWith(MockitoExtension.class)
//class DomainEventMongoRepositoryTest {
//
//    @Mock
//    private MongoTemplate mongoTemplate;
//
//    @Mock
//    private EventPublisher eventPublisher;
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private DomainEventMongoRepository domainEventMongoRepository;
//
//    @Captor
//    private ArgumentCaptor<DomainEventMongo> domainEventMongoCaptor;
//
//    @Test
//    @DisplayName("Given a collection of domain events when saveAndPublish is called then each event should be saved and published")
//    void saveAndPublishSuccessTest() throws JsonProcessingException {
//        // Given
//        TestDomainEvent event1 = new TestDomainEvent(UUID.randomUUID().toString(), Instant.now(), "data");
//        TestDomainEvent event2 = new TestDomainEvent(UUID.randomUUID().toString(), Instant.now(), "data");
//        List<DomainEvent> events = List.of(event1, event2);
//
//        when(objectMapper.writeValueAsString(any(DomainEvent.class))).thenReturn("{\"event\":\"data\"}");
//        when(mongoTemplate.save(any(DomainEventMongo.class), eq("domain_events"))).thenReturn(new DomainEventMongo());
//
//        // When
//        domainEventMongoRepository.saveAndPublish(events);
//
//        // Then
//        verify(mongoTemplate, times(2)).save(domainEventMongoCaptor.capture(), eq("domain_events"));
//        verify(eventPublisher).publish(event1);
//        verify(eventPublisher).publish(event2);
//
//        List<DomainEventMongo> captured = domainEventMongoCaptor.getAllValues();
//        assertThat(captured).hasSize(2);
//        assertThat(captured.get(0).eventId()).isEqualTo(event1.id());
//        assertThat(captured.get(0).eventType()).isEqualTo(TestDomainEvent.class.getName());
//        assertThat(captured.get(0).aggregateType()).isEqualTo(event1.aggregateType());
//        assertThat(captured.get(0).payload()).isEqualTo("{\"event\":\"data\"}");
//        assertThat(captured.get(0).occurredOn()).isEqualTo(event1.occurredOn());
//    }
//
//    @Test
//    @DisplayName("Given a domain event when saveAndPublish encounters a JsonProcessingException then it should throw RuntimeException")
//    void saveAndPublishJsonExceptionTest() throws JsonProcessingException {
//        // Given
//        TestDomainEvent event = new TestDomainEvent(UUID.randomUUID().toString(), Instant.now(), "data");
//        List<DomainEvent> events = List.of(event);
//
//        when(objectMapper.writeValueAsString(any(DomainEvent.class))).thenThrow(
//            new JsonProcessingException("Test error") {
//            });
//
//        // When, Then
//        assertThatThrownBy(() -> domainEventMongoRepository.saveAndPublish(events))
//            .isInstanceOf(RuntimeException.class)
//            .hasCauseInstanceOf(JsonProcessingException.class);
//
//        verify(mongoTemplate, never()).save(any(), any(String.class));
//        verify(eventPublisher, never()).publish(any());
//    }
//
//
//}
