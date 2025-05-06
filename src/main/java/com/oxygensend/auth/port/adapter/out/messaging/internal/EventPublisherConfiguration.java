package com.oxygensend.auth.port.adapter.out.messaging.internal;

import com.oxygensend.auth.port.Ports;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import common.ExcludeFromJacocoGeneratedReport;
import common.domain.model.EventPublisher;

@ExcludeFromJacocoGeneratedReport
@Profile(Ports.INTERNAL)
@Configuration
public class EventPublisherConfiguration {

    @Bean
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

}
