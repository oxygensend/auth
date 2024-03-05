package com.oxygensend.auth.config.properties;

import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.IdentityType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull IdentityType identity,
                                 @NotEmpty Set<String> roles,
                                 @Valid SettingsProperties.UserRoleProperties userRoleEndpoint) {

    @PostConstruct
    public void validate() {
        if (roles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }
    }

    public record SignInProperties(@NotNull Boolean requireEmailValidation,
                                   @NotNull Boolean onlyAdminsCanCreateUsers,
                                   @NotNull Boolean requireAdminValidation,
                                   AccountActivation accountActivation) {
    }

    public record UserRoleProperties(@NotNull Boolean enabled) {

    }

    //TODO("Add role independent settings")

}
