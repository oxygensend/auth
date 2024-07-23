package com.oxygensend.auth.application.jwt;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.application.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.application.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class JwtFacade {
    private final TokenStorage tokenStorage;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final Map<TokenType, Long> tokenExpirationMap;

    public JwtFacade(TokenStorage tokenStorage, TokenProperties tokenProperties, TokenPayloadFactoryProvider tokenPayloadFactory) {
        this.tokenStorage = tokenStorage;
        this.tokenPayloadFactory = tokenPayloadFactory;
        tokenExpirationMap = Map.of(
                TokenType.ACCESS, tokenProperties.authExpirationMs(),
                TokenType.REFRESH, tokenProperties.refreshExpirationMs(),
                TokenType.PASSWORD_RESET, TimeUnit.DAYS.toMillis(tokenProperties.passwordResetExpirationDays()),
                TokenType.EMAIL_VERIFICATION, TimeUnit.DAYS.toMillis(tokenProperties.emailVerificationExpirationDays())
        );
    }

    public String generateToken(User user, TokenType tokenType) {
        var exp = getExpiration(tokenType);
        TokenPayload payload = tokenPayloadFactory.createToken(tokenType,
                                                               new Date(System.currentTimeMillis() + exp),
                                                               new Date(System.currentTimeMillis()),
                                                               user);

        return tokenStorage.generateToken(payload);
    }

    public TokenPayload validateToken(String token, TokenType type) {
        return tokenStorage.validate(token, type);
    }


    private long getExpiration(TokenType tokenType) {
        var exp = tokenExpirationMap.get(tokenType);
        if (exp == null) {
            throw new IllegalArgumentException("Token type not supported");
        }

        return exp;
    }
}
