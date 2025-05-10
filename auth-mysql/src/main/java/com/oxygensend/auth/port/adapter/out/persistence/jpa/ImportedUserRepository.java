package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ImportedUserRepository extends JpaRepository<UserJpa, UUID> {
    Optional<UserJpa> findByEmail(String email);

    Optional<UserJpa> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
