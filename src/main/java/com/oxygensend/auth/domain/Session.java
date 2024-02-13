package com.oxygensend.auth.domain;


import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sessions")
public record Session(@Id UUID id) {
}