package com.oxygensend.auth.port.adapter.out.messaging.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import com.oxygensend.common.domain.model.EventPublisher;

@ExcludeFromJacocoGeneratedReport
@Configuration
public class EventPublisherConfiguration {

    @Bean
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

}
