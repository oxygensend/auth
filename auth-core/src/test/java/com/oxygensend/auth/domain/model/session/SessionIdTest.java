package com.oxygensend.auth.domain.model.session;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionIdTest {

    @Test
    void shouldCreateSessionIdWithUUID() {
        // Given
        UUID uuid = UUID.randomUUID();
        
        // When
        SessionId sessionId = new SessionId(uuid);
        
        // Then
        assertThat(sessionId.value()).isEqualTo(uuid);
        assertThat(sessionId.toString()).contains(uuid.toString());
    }
    
    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        assertThatThrownBy(() -> new SessionId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("SessionId value cannot be null");
    }
    
    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        UUID uuid = UUID.randomUUID();
        SessionId sessionId1 = new SessionId(uuid);
        SessionId sessionId2 = new SessionId(uuid);
        
        // Then
        assertThat(sessionId1).isEqualTo(sessionId2);
        assertThat(sessionId1.hashCode()).isEqualTo(sessionId2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        SessionId sessionId1 = new SessionId(UUID.randomUUID());
        SessionId sessionId2 = new SessionId(UUID.randomUUID());
        
        // Then
        assertThat(sessionId1).isNotEqualTo(sessionId2);
        assertThat(sessionId1.hashCode()).isNotEqualTo(sessionId2.hashCode());
    }
    
    @Test
    void shouldHaveCorrectToStringOutput() {
        // Given
        UUID uuid = UUID.randomUUID();
        SessionId sessionId = new SessionId(uuid);
        
        // When
        String result = sessionId.toString();
        
        // Then - records generate nice toString() implementations
        assertThat(result).contains("SessionId");
        assertThat(result).contains(uuid.toString());
    }
}
