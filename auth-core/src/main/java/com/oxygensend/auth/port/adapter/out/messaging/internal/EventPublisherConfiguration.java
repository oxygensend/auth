package com.oxygensend.auth.port.adapter.out.messaging.internal;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import com.oxygensend.common.domain.model.EventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventPublisher.class)
@ExcludeFromJacocoGeneratedReport
@Configuration
public class EventPublisherConfiguration {

    @Bean
    EventPublisher internalEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new InternalEventPublisher(applicationEventPublisher);
    }

    @Bean
    InternalDomainEventListener internalDomainEventListener() {
        return new InternalDomainEventListener();
    }

}
