package com.oxygensend.auth.domain.model.identity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdTest {

    @Test
    void shouldCreateUserIdFromUUID() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        UserId userId = new UserId(uuid);
        
        // Then
        assertThat(userId.value()).isEqualTo(uuid);
        assertThat(userId.toString()).isEqualTo(uuid.toString());
    }
    
    @Test
    void shouldCreateUserIdFromString() {
        // Given
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        
        // When
        UserId userId = new UserId(uuidString);
        
        // Then
        assertThat(userId.value()).isEqualTo(UUID.fromString(uuidString));
        assertThat(userId.toString()).isEqualTo(uuidString);
    }
    
    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        assertThatThrownBy(() -> new UserId((UUID) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UserId value cannot be null");
    }
    
    @Test
    void shouldThrowExceptionWhenStringIsInvalid() {
        // When/Then
        assertThatThrownBy(() -> new UserId("invalid-uuid"))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        UserId id1 = new UserId(uuidString);
        UserId id2 = new UserId(uuidString);
        
        // Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        UserId id1 = new UserId(UUID.randomUUID());
        UserId id2 = new UserId(UUID.randomUUID());
        
        // Then
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
    
    @Test
    void shouldHandleToStringConsistently() {
        // Given
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);
        
        // When
        String result = userId.toString();
        
        // Then
        assertThat(result).isEqualTo(uuid.toString());
    }
}
