package com.oxygensend.auth.application.business_callback;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

public class BusinessService {

    private final WebClient webClient;

    public BusinessService(@Qualifier("businessCallbackClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public BusinessId sendData(BusinessRequestDto requestDto) {
        var response = webClient.post()
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(BusinessResponseDto.class)
            .block();

        return new BusinessId(response.userId());
    }
}
