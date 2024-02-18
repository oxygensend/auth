package com.oxygensend.auth.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "services")
public record ServiceProperties(@Valid Notifications notifications) {
    public record Notifications(@NotNull @NotBlank String url,
                                @NotNull @NotBlank String login,
                                @NotNull @NotBlank String serviceId) {
    }
}
