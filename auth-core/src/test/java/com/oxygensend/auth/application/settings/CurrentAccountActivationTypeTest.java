package com.oxygensend.auth.application.settings;

import static org.assertj.core.api.Assertions.assertThat;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class CurrentAccountActivationTypeTest {

    @ParameterizedTest
    @EnumSource(AccountActivationType.class)
    void shouldReturnSameAccountActivationTypeAsProvided(AccountActivationType type) {
        // Given
        CurrentAccountActivationType currentType = new CurrentAccountActivationType(type);
        
        // When
        AccountActivationType result = currentType.get();
        
        // Then
        assertThat(result).isEqualTo(type);
    }


}
