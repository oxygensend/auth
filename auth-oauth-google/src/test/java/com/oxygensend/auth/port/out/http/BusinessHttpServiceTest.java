package com.oxygensend.auth.port.out.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.application.business_callback.BusinessRequestDto;
import com.oxygensend.auth.application.business_callback.BusinessResponseDto;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BusinessHttpServiceTest {

    private static final String BUSINESS_USER_ID = "business-123";
    private static final UserId USER_ID = new UserId(UUID.randomUUID());
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final EmailAddress EMAIL = new EmailAddress("john.doe@example.com");

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private BusinessHttpService businessHttpService;

    @BeforeEach
    void setUp() {
        businessHttpService = new BusinessHttpService(webClient);
    }

    @Test
    void sendData_shouldReturnBusinessId_whenSuccessfulRequest() {
        // Given
        BusinessRequestDto requestDto = new BusinessRequestDto(USER_ID, FIRST_NAME, LAST_NAME, EMAIL);
        BusinessResponseDto responseDto = new BusinessResponseDto(BUSINESS_USER_ID);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BusinessResponseDto.class)).thenReturn(Mono.just(responseDto));

        // When
        BusinessId result = businessHttpService.sendData(requestDto);

        // Then
        assertThat(result.value()).isEqualTo(BUSINESS_USER_ID);
    }
}