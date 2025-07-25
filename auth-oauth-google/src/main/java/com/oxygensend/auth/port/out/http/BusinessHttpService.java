package com.oxygensend.auth.port.out.http;

import com.oxygensend.auth.application.business_callback.BusinessRequestDto;
import com.oxygensend.auth.application.business_callback.BusinessResponseDto;
import com.oxygensend.auth.application.business_callback.BusinessService;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
class BusinessHttpService implements BusinessService {
    private final WebClient webClient;

    BusinessHttpService(@Qualifier("businessCallbackClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BusinessId sendData(BusinessRequestDto requestDto) {
        var response = webClient.post()
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(BusinessResponseDto.class)
            .block();

        return new BusinessId(response.userId());
    }
}
