package com.oxygensend.auth.config;

import com.oxygensend.auth.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry.requestMatchers("/swagger-ui/**",
                                                                            "/manage/**",
                                                                            "/v3/api-docs/**",
                                                                            "/v1/auth/**",
                                                                            "/v1/users/verify_email",
                                                                            "/v1/users/reset_password",
                                                                            "/v1/users/create",
                                                                            "/v1/users/generate_password_reset_token",
                                                                            "/v1/users/generate_email_verification_token")
                                                           .permitAll()
                                                           .anyRequest()
                                                           .authenticated())
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

                )
                .exceptionHandling(configurer -> configurer.accessDeniedHandler(
                        (request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value())))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
