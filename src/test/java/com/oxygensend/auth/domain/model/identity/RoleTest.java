package com.oxygensend.auth.domain.model.identity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    void shouldCreateRoleSuccessfully() {
        // Given
        String roleValue = "ADMIN";
        
        // When
        Role role = UserMother.role(roleValue);
        
        // Then
        assertThat(role.value()).isEqualTo(roleValue);
        assertThat(role.toString()).isEqualTo(roleValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionWhenValueIsInvalid(String invalidValue) {
        // When/Then
        assertThatThrownBy(() -> new Role(invalidValue))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Role value cannot be empty");
    }
    
    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        Role role1 = new Role("USER");
        Role role2 = new Role("USER");
        
        // Then
        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        Role role1 = new Role("USER");
        Role role2 = new Role("ADMIN");
        
        // Then
        assertThat(role1).isNotEqualTo(role2);
        assertThat(role1.hashCode()).isNotEqualTo(role2.hashCode());
    }
}
