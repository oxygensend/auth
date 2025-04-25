package com.oxygensend.auth.config;

import com.oxygensend.auth.application.auth.LoginProvider;
import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.config.properties.SettingsProperties;
import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.infrastructure.security.DomainUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties( {TokenProperties.class, SettingsProperties.class})
public class AppConfiguration {


    @Bean
    LoginProvider loginProvider(SettingsProperties settingsProperties) {
        return new LoginProvider(settingsProperties.identity());
    }
    @Bean
    UserDetailsService userDetailsService(UserService userService, LoginProvider loginProvider) {
      return new DomainUserDetailsService(userService, loginProvider);
    }

    //FIXME add encryption level to config
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }


}
