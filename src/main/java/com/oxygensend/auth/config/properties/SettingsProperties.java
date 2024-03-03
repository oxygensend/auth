package com.oxygensend.auth.config.properties;

import com.oxygensend.auth.domain.AccountActivation;
import com.oxygensend.auth.domain.IdentityType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull IdentityType identity,
                                 @Valid SettingsProperties.UserRoleProperties userRoleEndpoint) {


    public record SignInProperties(@NotNull Boolean requireEmailValidation,
                                   @NotNull Boolean onlyAdminsCanCreateUsers,
                                   @NotNull Boolean requireAdminValidation,
                                   AccountActivation accountActivation) {
    }

    public record UserRoleProperties(@NotNull Boolean enabled) {

    }

    //TODO("Add role independent settings")

}
