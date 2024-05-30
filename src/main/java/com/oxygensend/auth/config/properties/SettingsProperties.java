package com.oxygensend.auth.config.properties;

import com.oxygensend.auth.config.EventBroker;
import com.oxygensend.auth.config.IdentityType;
import com.oxygensend.auth.domain.AccountActivation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull IdentityType identity,
                                 @NotEmpty Set<String> roles,
                                 @Valid SettingsProperties.UserRoleProperties userRoleEndpoint,
                                 @NotNull EventBroker eventBroker) {
    @PostConstruct
    public void validate() {
        if (roles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }

        if (signIn.accountActivation != AccountActivation.NONE && signIn.registerEventTopic == null) {
            throw new IllegalArgumentException("Kafka topic is required for AccountActivation != NONE");
        }
    }

    public record SignInProperties(AccountActivation accountActivation,
                                   String registerEventTopic) {
    }

    public record UserRoleProperties(@NotNull Boolean enabled) {

    }


}
