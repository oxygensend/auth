package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TokenPayloadFactoryProvider {

    private final Map<TokenType, TokenPayloadFactory> factoryMap;

    TokenPayloadFactoryProvider(List<TokenPayloadFactory> factories) {
        this.factoryMap = factories.stream()
                                   .collect(Collectors.toMap(TokenPayloadFactory::getType, factory -> factory, detectDuplicatedImplementations()));
    }

    public TokenPayload createPayload(TokenType type, Date exp, Date iat, TokenSubject subject) {
        return getFactory(type).createPayload(exp, iat, subject);
    }

    public TokenPayload createPayload(TokenType type, Claims claims) {
        return getFactory(type).createPayload(claims);
    }

    private TokenPayloadFactory getFactory(TokenType type) {
        return Optional.ofNullable(factoryMap.get(type)).orElseThrow(() -> new RuntimeException("No factory found for token type: " + type));
    }

    private BinaryOperator<TokenPayloadFactory> detectDuplicatedImplementations() {
        return (l, r) -> {
            throw new RuntimeException("Found duplicated strategies assigned to one token type value: " + l.getClass() + " " + r.getClass());
        };
    }
}
