package com.oxygensend.auth.port.adapter.out.messaging.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import common.ExcludeFromJacocoGeneratedReport;
import common.domain.model.EventPublisher;

@ExcludeFromJacocoGeneratedReport
@Configuration
public class EventPublisherConfiguration {

    @Bean
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

}
