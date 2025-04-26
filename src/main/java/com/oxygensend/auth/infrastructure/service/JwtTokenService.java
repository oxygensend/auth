package com.oxygensend.auth.infrastructure.service;

import com.oxygensend.auth.infrastructure.spring.config.properties.TokenProperties;
import com.oxygensend.auth.domain.model.token.exception.TokenException;
import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import com.oxygensend.auth.domain.model.token.payload.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
final class JwtTokenService implements TokenService {
    private final TokenPayloadFactoryProvider tokenPayloadFactory;
    private final TokenProperties tokenProperties;

    public JwtTokenService(TokenPayloadFactoryProvider tokenPayloadFactory, TokenProperties tokenProperties) {
        this.tokenPayloadFactory = tokenPayloadFactory;
        this.tokenProperties = tokenProperties;
    }

    @Override
    public String createToken(TokenPayload payload) {
        return Jwts.builder()
                   .claims(payload.toClaims())
                   .signWith(tokenProperties.getSignInKey())
                   .compact();
    }

    @Override
    public TokenPayload parseToken(String token, TokenType type) {
        Claims claims = extractClaims(token);
        TokenPayload payload = tokenPayloadFactory.createPayload(type, claims);

        if (payload.type() != type) {
            throw new TokenException("Invalid token type");
        }

        return payload;
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                   .verifyWith(tokenProperties.getSignInKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
}
