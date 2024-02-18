package com.oxygensend.auth.infrastructure.service.notifications;


import com.oxygensend.auth.config.properties.ServiceProperties;
import com.oxygensend.auth.domain.NotificationRepository;
import com.oxygensend.commons_jdk.feign.ClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServiceProperties.class)
public class NotificationsConfiguration {

    @Bean
    NotificationRepository notificationRepository(ServiceProperties serviceProperties, NotificationsClient notificationsClient) {
        return new NotificationRestRepository(notificationsClient,
                                              serviceProperties.notifications().login(),
                                              serviceProperties.notifications().serviceId());
    }

    @Bean
    NotificationsClient notificationsClient(ClientFactory clientFactory, @Value("${services.notifications.url}") String url) {
        return clientFactory.create(url, NotificationsClient.class, builder -> {
        });
    }
}

