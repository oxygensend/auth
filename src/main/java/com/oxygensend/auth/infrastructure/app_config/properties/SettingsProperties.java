package com.oxygensend.auth.infrastructure.app_config.properties;

import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull LoginType loginType,
                                 @NotEmpty Set<String> roles,
                                 @Valid SettingsProperties.UserRoleProperties userRoleFeature) {
    @PostConstruct
    public void validate() {
        if (roles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }

    }

    public record SignInProperties(AccountActivationType accountActivation,
                                   String registerEventTopic) {
    }

    public record UserRoleProperties(@NotNull Boolean enabled) {

    }


}
