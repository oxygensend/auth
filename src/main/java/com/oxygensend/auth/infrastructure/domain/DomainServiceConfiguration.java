package com.oxygensend.auth.infrastructure.domain;

import com.oxygensend.auth.domain.model.identity.UserUniquenessChecker;
import com.oxygensend.auth.domain.model.identity.AuthenticationService;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import com.oxygensend.auth.infrastructure.app_config.properties.TokenProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Configuration
class DomainServiceConfiguration {

    @Bean
    AuthenticationService authenticationService(PasswordService passwordService,
                                                UserRepository userRepository) {
        return new AuthenticationService(passwordService, userRepository);
    }

    @Bean
    UserUniquenessChecker userUniquenessChecker(UserRepository userRepository) {
        return new UserUniquenessChecker(userRepository);
    }

    @Bean
    PasswordService passwordService(PasswordEncoder passwordEncoder) {
        return new BcryptPasswordService(passwordEncoder);
    }

    @Bean
    TokenService tokenService(TokenPayloadFactoryProvider tokenPayloadFactoryProvider,
                              TokenProperties tokenProperties) {
        return new JwtTokenService(tokenPayloadFactoryProvider, tokenProperties);
    }

}
