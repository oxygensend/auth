package com.oxygensend.auth.context.auth.jwt;

import com.oxygensend.auth.config.properties.TokenProperties;
import com.oxygensend.auth.context.auth.jwt.factory.TokenPayloadFactoryProvider;
import com.oxygensend.auth.context.auth.jwt.payload.RefreshTokenPayload;
import com.oxygensend.auth.context.auth.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.TokenType;
import com.oxygensend.auth.domain.exception.TokenException;
import com.oxygensend.auth.helper.TokenHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenStorageTest {

    @InjectMocks
    private TokenStorage tokenStorage;
    @Mock
    private TokenPayloadFactoryProvider tokenPayloadFactory;

    @Mock
    private TokenProperties tokenProperties;


    @Test
    public void testGenerateToken() {
        // Arrange
        Date currentDate = new Date();
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        var payload = new RefreshTokenPayload(UUID.randomUUID(), currentDate, expDate);

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());

        // Act
        String token = tokenStorage.generateToken(payload);

        // Assert
        assertNotNull(token);
        verify(tokenProperties, times(1)).getSignInKey();
    }

    @Test
    public void testValidate_ValidTokenAndType() {
        // Arrange

        TokenType type = TokenType.REFRESH;
        Claims claims = Jwts.claims()
                            .subject(UUID.randomUUID().toString())
                            .expiration(new Date(System.currentTimeMillis() + 3600))
                            .issuedAt(new Date())
                            .add("type", type)
                            .build();


        String token = Jwts.builder()
                           .subject(claims.getSubject())
                           .expiration(claims.getExpiration())
                           .issuedAt(claims.getIssuedAt())
                           .claims()
                           .add("type", type)
                           .and()
                           .signWith(TokenHelper.createSigningKey())
                           .compact();
        TokenPayload expectedPayload = new RefreshTokenPayload(UUID.fromString(claims.getSubject()), claims.getIssuedAt(), claims.getExpiration());

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());
        when(tokenPayloadFactory.createToken(any(TokenType.class), any(Claims.class))).thenReturn(expectedPayload);


        // Act
        TokenPayload result = tokenStorage.validate(token, type);

        // Assert
        assertEquals(expectedPayload, result);
    }

    @Test
    public void testValidate_ThrowException() {
        // Arrange

        TokenType type = TokenType.REFRESH;
        Claims claims = Jwts.claims()
                            .subject(UUID.randomUUID().toString())
                            .expiration(new Date(System.currentTimeMillis() + 3600))
                            .issuedAt(new Date())
                            .add("type", type)
                            .build();


        String token = Jwts.builder()
                           .subject(claims.getSubject())
                           .expiration(claims.getExpiration())
                           .issuedAt(claims.getIssuedAt())
                           .claims()
                           .add("type", type)
                           .and()
                           .signWith(TokenHelper.createSigningKey())
                           .compact();
        TokenPayload expectedPayload = new RefreshTokenPayload(UUID.fromString(claims.getSubject()), claims.getIssuedAt(), claims.getExpiration());

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());
        when(tokenPayloadFactory.createToken(any(TokenType.class), any(Claims.class))).thenReturn(expectedPayload);


        // Act
        assertThrows(TokenException.class, () -> tokenStorage.validate(token, TokenType.ACCESS));

    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public TokenProperties tokenProperties() {
            var secretKey = "614E645267556B58703273357638792F413F4428472B4B6250655368566D597133743677397A244326452948404D635166546A576E5A7234753778214125442A";
            return new TokenProperties(secretKey, 3600000, 86400000, 123);
        }
    }
}
