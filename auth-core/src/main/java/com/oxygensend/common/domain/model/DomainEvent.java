package com.oxygensend.common.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;

import java.time.Instant;

@ExcludeFromJacocoGeneratedReport
public interface DomainEvent {

    default String id() {
        return String.valueOf(System.currentTimeMillis());
    }

    default String aggregateType() {
        return User.class.getName();
    }


    @JsonProperty("occurredOn")
    default Instant occurredOn() {
        return Instant.now();
    }
}
