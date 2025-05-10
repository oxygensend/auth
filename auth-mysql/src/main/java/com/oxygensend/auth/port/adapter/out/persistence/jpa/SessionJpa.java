package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@ExcludeFromJacocoGeneratedReport
@Entity(name = "sessions")
class SessionJpa {
    @Id
    public UUID id;

    @Column(nullable = false)
    public UUID userId;

    public SessionJpa() {
    }

    public SessionJpa(UUID id, UUID userId) {
        this.id = id;
        this.userId = userId;
    }
}

