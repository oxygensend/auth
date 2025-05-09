package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.port.Ports;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Profile;

import java.time.Instant;

@Entity(name = "domain_events")
public class DomainEventJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String eventId;

    @Column(nullable = false)
    public String aggregateType;

    @Column(nullable = false)
    public String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String eventPayload;

    @Column(nullable = false)
    public Instant occurredOn;

    public DomainEventJpa() {
    }

    public DomainEventJpa(String eventId, String aggregateType, String eventType, String eventPayload,
                          Instant occurredOn) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.occurredOn = occurredOn;
    }

}
