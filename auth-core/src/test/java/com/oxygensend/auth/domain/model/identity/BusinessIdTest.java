package com.oxygensend.auth.domain.model.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BusinessIdTest {

    @Test
    void shouldCreateBusinessIdSuccessfully() {
        // Given
        String value = "B12345";
        
        // When
        BusinessId businessId = UserMother.businessId(value);
        
        // Then
        assertThat(businessId.value()).isEqualTo(value);
        assertThat(businessId.toString()).isEqualTo(value);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenValueIsInvalid(String invalidValue) {
        // When/Then
        assertThatThrownBy(() -> new BusinessId(invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("BusinessId value cannot be empty");
    }
    
    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        BusinessId id1 = new BusinessId("B12345");
        BusinessId id2 = new BusinessId("B12345");
        
        // Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        BusinessId id1 = new BusinessId("B12345");
        BusinessId id2 = new BusinessId("B67890");
        
        // Then
        assertThat(id1).isNotEqualTo(id2);
        assertThat(id1.hashCode()).isNotEqualTo(id2.hashCode());
    }
}
