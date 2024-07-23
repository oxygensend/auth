package com.oxygensend.auth.application.jwt;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.application.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.application.jwt.payload.ClaimsPayload;
import com.oxygensend.auth.application.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
final class TokenStorage {

    private final TokenProperties tokenProperties;
    private final TokenPayloadFactoryProvider tokenPayloadFactory;

    public String generateToken(ClaimsPayload payload) {

        return Jwts.builder()
                   .claims(payload.toClaims())
                   .signWith(tokenProperties.getSignInKey())
                   .compact();
    }


    public TokenPayload validate(String token, TokenType type) {
        Claims claims = extractClaims(token);
        TokenPayload payload = tokenPayloadFactory.createToken(type, claims);

        if (payload.type() != type) {
            throw new TokenException("Invalid token");
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