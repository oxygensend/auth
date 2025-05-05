package com.oxygensend.auth.infrastructure.app_config;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
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
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
@EnableConfigurationProperties({TokenProperties.class, SettingsProperties.class})
public class AppConfiguration {

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
