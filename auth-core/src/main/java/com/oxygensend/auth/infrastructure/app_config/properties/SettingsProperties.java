package com.oxygensend.auth.infrastructure.app_config.properties;

import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Stream;

import common.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
@Validated
@ConfigurationProperties(prefix = "auth.settings")
public record SettingsProperties(@Valid SettingsProperties.SignInProperties signIn,
                                 @NotNull LoginType loginType,
                                 @NotEmpty List<String> roles,
                                 @NotEmpty List<String> adminRoles
) {
    @PostConstruct
    public void validate() {
        if (roles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }

        if (adminRoles.stream().anyMatch(role -> !role.startsWith("ROLE_"))) {
            throw new IllegalArgumentException("Roles must start with ROLE_");
        }

        if (roles.stream().anyMatch(adminRoles::contains)) {
            throw new IllegalArgumentException("Admin roles must not be in the regular roles list");
        }

    }

    public List<String> allRoles() {
        return Stream.of(roles, adminRoles)
                     .flatMap(List::stream)
                     .toList();
    }

    public record SignInProperties(AccountActivationType accountActivation) {
    }


}
