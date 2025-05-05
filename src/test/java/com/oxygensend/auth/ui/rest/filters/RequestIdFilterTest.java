package com.oxygensend.auth.ui.rest.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestIdFilterTest {

    private RequestIdFilter requestIdFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        requestIdFilter = new RequestIdFilter();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUseRequestIdFromHeaderWhenProvided() throws ServletException, IOException {
        // Given
        String requestId = "test-request-id";
        when(request.getHeader("Request-Id")).thenReturn(requestId);

        // When
        requestIdFilter.doFilter(request, response, filterChain);

        // Then
        assertThat(MDC.get("Request-Id")).isEqualTo(requestId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldGenerateRequestIdWhenHeaderNotProvided() throws ServletException, IOException {
        // Given
        when(request.getHeader("Request-Id")).thenReturn(null);

        // When
        requestIdFilter.doFilter(request, response, filterChain);

        // Then
        String generatedRequestId = MDC.get("Request-Id");
        assertThat(generatedRequestId).isNotNull();
        assertThat(generatedRequestId).startsWith("[");
        assertThat(generatedRequestId).endsWith("]");
        assertThat(generatedRequestId).hasSizeGreaterThan(2); // Contains more than just brackets
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldCallNextFilterInChain() throws ServletException, IOException {
        // Given
        when(request.getHeader("Request-Id")).thenReturn("test-request-id");

        // When
        requestIdFilter.doFilter(request, response, filterChain);

        // Then
        ArgumentCaptor<HttpServletRequest> requestCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        ArgumentCaptor<ServletResponse> responseCaptor = ArgumentCaptor.forClass(ServletResponse.class);
        
        verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());
        
        assertThat(requestCaptor.getValue()).isEqualTo(request);
        assertThat(responseCaptor.getValue()).isEqualTo(response);
    }

    @Test
    void shouldHaveHighestPrecedenceOrder() {
        // When
        int order = requestIdFilter.getOrder();

        // Then
        assertThat(order).isEqualTo(Integer.MIN_VALUE);
    }
}
