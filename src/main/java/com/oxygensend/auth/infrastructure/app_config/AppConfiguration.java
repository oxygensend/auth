package com.oxygensend.auth.infrastructure.app_config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oxygensend.auth.application.settings.CurrentAccountActivationType;
import com.oxygensend.auth.application.settings.LoginProvider;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import com.oxygensend.auth.infrastructure.app_config.properties.SettingsProperties;
import com.oxygensend.auth.infrastructure.app_config.properties.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Configuration
@EnableConfigurationProperties({TokenProperties.class, SettingsProperties.class})
class AppConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    LoginProvider loginProvider(SettingsProperties settingsProperties) {
        return new LoginProvider(settingsProperties.loginType());
    }


    @Bean
    TokenApplicationService tokenApplicationService(TokenProperties tokenProperties,
                                                    TokenService tokenService,
                                                    TokenPayloadFactoryProvider tokenPayloadFactory) {
        return new TokenApplicationService(tokenService,
                                           tokenProperties.expiration(),
                                           tokenPayloadFactory);
    }

    @Bean
    CurrentAccountActivationType currentAccountActivationType(SettingsProperties settingsProperties) {
        return new CurrentAccountActivationType(settingsProperties.signIn().accountActivation());
    }

}
