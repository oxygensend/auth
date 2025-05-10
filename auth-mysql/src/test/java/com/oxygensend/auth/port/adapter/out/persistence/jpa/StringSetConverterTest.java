package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringSetConverterTest {

    private final StringSetConverter converter = new StringSetConverter();

    @Test
    @DisplayName("Given a set of strings when converting to database column then should join with delimiter")
    void convertToDatabaseColumn_shouldJoinWithDelimiter() {
        // Given
        Set<String> strings = Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER");

        // When
        String result = converter.convertToDatabaseColumn(strings);

        // Then
        // Check each role is present in the result, separated by delimiter 
        for (String role : strings) {
            assertThat(result).contains(role);
        }
        
        // Count the delimiters
        long delimiterCount = result.chars().filter(ch -> ch == ';').count();
        assertThat(delimiterCount).isEqualTo(strings.size() - 1);
    }

    @ParameterizedTest
    @MethodSource("provideStringsForConversion")
    @DisplayName("Given a delimited string when converting to entity attribute then should split into set")
    void convertToEntityAttribute_shouldSplitIntoSet(String dbData, Set<String> expected) {
        // When
        Set<String> result = converter.convertToEntityAttribute(dbData);

        // Then
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }
    
    @ParameterizedTest
    @NullSource
    @DisplayName("Given null when converting to entity attribute then should return empty set")
    void convertToEntityAttribute_givenNull_shouldReturnEmptySet(String nullValue) {
        // When
        Set<String> result = converter.convertToEntityAttribute(nullValue);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Given empty set when converting to database column then should return empty string")
    void convertToDatabaseColumn_givenEmptySet_shouldReturnEmptyString() {
        // Given
        Set<String> emptySet = new HashSet<>();
        
        // When
        String result = converter.convertToDatabaseColumn(emptySet);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    static Stream<Arguments> provideStringsForConversion() {
        return Stream.of(
            Arguments.of("ROLE_USER;ROLE_ADMIN;ROLE_MANAGER", 
                         Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER")),
            Arguments.of("SINGLE_ROLE", Set.of("SINGLE_ROLE")),
            Arguments.of("", new HashSet<String>()),
            Arguments.of("ROLE_USER;;ROLE_ADMIN", Set.of("ROLE_USER", "ROLE_ADMIN"))
        );
    }
}
