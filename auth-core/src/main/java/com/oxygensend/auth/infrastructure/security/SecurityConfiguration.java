package com.oxygensend.auth.infrastructure.security;

import com.oxygensend.auth.application.auth.security.AuthenticationPrinciple;
import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.application.settings.LoginProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Configuration
class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

    @Bean
    UserDetailsService userDetailsService(UserService userService, LoginProvider loginProvider) {
        return new DomainUserDetailsService(userService, loginProvider);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                  PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    AuthenticationPrinciple authenticationPrinciple() {
        return new SpringSecurityAuthenticationPrinciple();
    }
}
