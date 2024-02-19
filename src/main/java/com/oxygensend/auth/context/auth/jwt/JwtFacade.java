package com.oxygensend.auth.context.auth.jwt;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.context.auth.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JwtFacade {
    private final TokenStorage tokenStorage;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final Map<TokenType, Integer> tokenExpirationMap;

    public JwtFacade(TokenStorage tokenStorage, TokenProperties tokenProperties, TokenPayloadFactoryProvider tokenPayloadFactory) {
        this.tokenStorage = tokenStorage;
        this.tokenPayloadFactory = tokenPayloadFactory;
        tokenExpirationMap = Map.of(
                TokenType.ACCESS, tokenProperties.authExpirationMs(),
                TokenType.REFRESH, tokenProperties.refreshExpirationMs(),
                TokenType.PASSWORD_RESET, Duration.ofDays(tokenProperties.passwordResetExpirationDays()).toMillisPart(),
                TokenType.EMAIL_VERIFICATION, Duration.ofDays(tokenProperties.emailVerificationExpirationDays()).toMillisPart()
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


    private int getExpiration(TokenType tokenType) {
        var exp = tokenExpirationMap.get(tokenType);
        if (exp == null) {
            throw new IllegalArgumentException("Token type not supported");
        }

        return exp;
    }
}
