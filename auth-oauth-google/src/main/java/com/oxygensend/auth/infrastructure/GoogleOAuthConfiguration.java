package com.oxygensend.auth.infrastructure;

import com.oxygensend.auth.application.GoogleOAuthData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GoogleOAuthConfig.class)
public class GoogleOAuthConfiguration {


    @Bean
    GoogleOAuthData googleOAuthConnection(GoogleOAuthConfig config) {
        return new GoogleOAuthData(config.clientId(),
                                   config.clientSecret(),
                                   config.redirectUri(),
                                   config.businessCallbackUrl(),
                                   config.authUrl(),
                                   null);
    }


    @Bean
    @Qualifier("businessCallbackClient")
    WebClient businessCallbackClient(GoogleOAuthConfig config) {
        return WebClient.builder()
            .baseUrl(config.businessCallbackUrl())
            .build();
    }
}
