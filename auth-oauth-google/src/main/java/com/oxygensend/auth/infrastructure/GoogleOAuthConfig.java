package com.oxygensend.auth.infrastructure;

import com.oxygensend.common.ExcludeFromJacocoGeneratedReport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ExcludeFromJacocoGeneratedReport
@Validated
@ConfigurationProperties(prefix = "auth.oauth.google")
public record GoogleOAuthConfig(@NotNull @NotBlank String clientId,
                                @NotNull @NotBlank String clientSecret,
                                @NotNull @NotBlank String redirectUri,
                                @NotBlank String businessCallbackUrl,
                                @NotNull @NotBlank String authUrl
) {
}
