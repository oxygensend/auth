package com.oxygensend.auth.domain.model.token.payload;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class TokenPayloadFactoryProviderTest {

    private TokenPayloadFactoryProvider provider;

    @Mock
    private RefreshTokenPayloadFactory refreshTokenPayloadFactory;
    @Mock
    private AccessTokenPayloadFactory accessTokenPayloadFactory;


    @BeforeEach
    void setUp() {

        when(refreshTokenPayloadFactory.getType()).thenReturn(TokenType.REFRESH);
        when(accessTokenPayloadFactory.getType()).thenReturn(TokenType.ACCESS);
        this.provider = new TokenPayloadFactoryProvider(List.of(refreshTokenPayloadFactory, accessTokenPayloadFactory));
    }

    @Test
    void createToken_callsFactoryMethod() {
        Date exp = new Date();
        Date iat = new Date();
        AccessTokenSubject subject = mock(AccessTokenSubject.class);
        TokenType type = TokenType.ACCESS;


        provider.createPayload(type, exp, iat, subject);

        verify(accessTokenPayloadFactory, times(1)).createPayload(exp, iat, subject);
        verify(refreshTokenPayloadFactory, never()).createPayload(exp, iat, subject);
    }

    @Test
    void createToken_withClaims_callsFactoryMethod() {
        Claims claims = mock(Claims.class);
        TokenType type = TokenType.REFRESH;


        provider.createPayload(type, claims);

        verify(refreshTokenPayloadFactory, times(1)).createPayload(claims);
        verify(accessTokenPayloadFactory, never()).createPayload(claims);
    }


    @Test
    void constructor_withDuplicateFactories_throwsException() {
        List<TokenPayloadFactory> duplicateFactories =
            Arrays.asList(accessTokenPayloadFactory, accessTokenPayloadFactory);

        assertThrows(RuntimeException.class, () -> new TokenPayloadFactoryProvider(duplicateFactories));
    }

    @Test
    void testCreateTokenTokenNotSpecified() {
        Claims claims = mock(Claims.class);
        assertThrows(RuntimeException.class, () -> provider.createPayload(TokenType.NOT_SPECIFIED, claims));

    }
}
