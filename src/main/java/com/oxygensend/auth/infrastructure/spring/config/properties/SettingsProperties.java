package com.oxygensend.auth.infrastructure.spring.config.properties;

import com.oxygensend.auth.infrastructure.spring.config.EventBroker;
import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull LoginType identity,
                                 @NotEmpty Set<String> roles,
                                 @Valid SettingsProperties.UserRoleProperties userRoleFeature,
                                 @NotNull EventBroker eventBroker) {
    @PostConstruct
    public void validate() {
        if (roles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }

        if (signIn.accountActivation != AccountActivationType.NONE && signIn.registerEventTopic == null) {
            throw new IllegalArgumentException("Kafka topic is required for AccountActivation != NONE");
        }
    }

    public record SignInProperties(AccountActivationType accountActivation,
                                   String registerEventTopic) {
    }

    public record UserRoleProperties(@NotNull Boolean enabled) {

    }


}
