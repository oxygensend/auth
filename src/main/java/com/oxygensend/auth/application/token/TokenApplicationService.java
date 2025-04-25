package com.oxygensend.auth.application.token;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import com.oxygensend.auth.domain.model.token.payload.TokenPayload;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TokenApplicationService {
    private final TokenService tokenService;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final Map<TokenType, Duration> tokenExpirationMap;

    public TokenApplicationService(TokenService tokenService, TokenProperties tokenProperties, TokenPayloadFactoryProvider tokenPayloadFactory) {
        this.tokenService = tokenService;
        this.tokenPayloadFactory = tokenPayloadFactory;
        this.tokenExpirationMap = tokenProperties.expiration();
    }

    public String createToken(TokenSubject subject, TokenType tokenType) {
        var exp = getExpiration(tokenType);
        TokenPayload payload = tokenPayloadFactory.createPayload(tokenType,
                                                                 new Date(System.currentTimeMillis() + exp),
                                                                 new Date(System.currentTimeMillis()),
                                                                 subject);

        return tokenService.createToken(payload);
    }

    public TokenPayload parseToken(String token, TokenType type) {
        return tokenService.parseToken(token, type);
    }


    private long getExpiration(TokenType tokenType) {
        var exp = tokenExpirationMap.get(tokenType);
        if (exp == null) {
            throw new IllegalArgumentException("Token type not supported");
        }

        return exp.toMillis();
    }
}
