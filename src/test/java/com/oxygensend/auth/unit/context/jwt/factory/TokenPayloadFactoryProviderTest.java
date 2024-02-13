package com.oxygensend.auth.unit.context.jwt.factory;

import com.oxygensend.auth.context.jwt.factory.AccessTokenPayloadFactory;
import com.oxygensend.auth.context.jwt.factory.RefreshTokenPayloadFactory;
import com.oxygensend.auth.context.jwt.factory.TokenPayloadFactory;
import com.oxygensend.auth.context.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        User user = mock(User.class);
        TokenType type = TokenType.ACCESS;


        provider.createToken(type, exp, iat, user);

        verify(accessTokenPayloadFactory, times(1)).createToken(exp, iat, user);
        verify(refreshTokenPayloadFactory, never()).createToken(exp, iat, user);
    }

    @Test
    void createToken_withClaims_callsFactoryMethod() {
        Claims claims = mock(Claims.class);
        TokenType type = TokenType.REFRESH;


        provider.createToken(type, claims);

        verify(refreshTokenPayloadFactory, times(1)).createToken(claims);
        verify(accessTokenPayloadFactory, never()).createToken(claims);
    }


    @Test
    void constructor_withDuplicateFactories_throwsException() {
        List<TokenPayloadFactory> duplicateFactories = Arrays.asList(accessTokenPayloadFactory, accessTokenPayloadFactory);

        assertThrows(RuntimeException.class, () -> new TokenPayloadFactoryProvider(duplicateFactories));
    }

    @Test
    void testCreateTokenTokenNotSpecified() {
        Claims claims = mock(Claims.class);
        assertThrows(RuntimeException.class, () -> provider.createToken(TokenType.NOT_SPECIFIED, claims));

    }
}
