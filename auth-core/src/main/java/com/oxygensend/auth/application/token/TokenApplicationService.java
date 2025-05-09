package com.oxygensend.auth.application.token;

import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.TokenPayload;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

public class TokenApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenApplicationService.class);
    private final TokenService tokenService;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final Map<TokenType, Duration> tokenExpirationMap;

    public TokenApplicationService(TokenService tokenService, Map<TokenType, Duration> tokenExpirationMap,
                                   TokenPayloadFactoryProvider tokenPayloadFactory) {
        this.tokenService = tokenService;
        this.tokenPayloadFactory = tokenPayloadFactory;
        this.tokenExpirationMap = tokenExpirationMap;
    }

    public String createToken(TokenSubject subject, TokenType tokenType) {
        LOGGER.info("Creating token for subject: {} and type: {}", subject, tokenType);
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
            throw new InvalidTokenException();
        }

        return exp.toMillis();
    }
}
