package com.oxygensend.auth.infrastructure.app_config.properties;

import com.oxygensend.auth.domain.model.token.TokenType;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.AssertTrue; // added for custom validation
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "security.token")
public record TokenProperties(@NotNull @NotBlank String secretKey,
                              @NotNull Map<TokenType, Duration> expiration) {

    @AssertTrue(message = "Expiration must be set for all token types except NOT_SPECIFIED.")
    private boolean isExpirationValid() {
        for (TokenType token : TokenType.values()) {
            if (token == TokenType.NOT_SPECIFIED) continue;
            Duration exp = expiration.get(token);
            if (exp == null || exp.isZero() || exp.isNegative()) {
                return false;
            }
        }
        return true;
    }

    public SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
