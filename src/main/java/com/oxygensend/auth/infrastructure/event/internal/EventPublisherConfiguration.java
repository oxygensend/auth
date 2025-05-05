package com.oxygensend.auth.infrastructure.event.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import common.event.EventPublisher;

@Configuration
@Profile("!KAFKA")
public class EventPublisherConfiguration {

    @Bean
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

}
