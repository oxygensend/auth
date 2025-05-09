package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ImportedSessionRepository extends JpaRepository<SessionJpa, UUID> {
    void deleteByUserId(UUID userId);

    Optional<SessionJpa> findByUserId(UUID userId);
}
