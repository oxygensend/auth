package com.oxygensend.auth.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.exception.TokenException;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import com.oxygensend.auth.domain.model.token.payload.TokenPayload;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import com.oxygensend.auth.helper.TokenHelper;
import com.oxygensend.auth.helper.UserMother;
import com.oxygensend.auth.infrastructure.spring.config.properties.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService tokenService;
    @Mock
    private TokenPayloadFactoryProvider tokenPayloadFactory;

    @Mock
    private TokenProperties tokenProperties;


    @Test
    public void testCreateToken() {
        // Arrange
        Date currentDate = new Date();
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        var payload = new RefreshTokenPayload(UserMother.getRandomUserId(), currentDate, expDate);

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());

        // Act
        String token = tokenService.createToken(payload);

        // Assert
        assertNotNull(token);
        verify(tokenProperties, times(1)).getSignInKey();
    }

    @Test
    public void testParseTokeb_ValidTokenAndType() {
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
        TokenPayload expectedPayload = new RefreshTokenPayload(new UserId(UUID.fromString(claims.getSubject())),
                                                               claims.getIssuedAt(),
                                                               claims.getExpiration());

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());
        when(tokenPayloadFactory.createPayload(any(TokenType.class), any(Claims.class))).thenReturn(expectedPayload);


        // Act
        TokenPayload result = tokenService.parseToken(token, type);

        // Assert
        assertEquals(expectedPayload, result);
    }

    @Test
    public void testParseToken_ThrowException() {
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
        TokenPayload expectedPayload =
            new RefreshTokenPayload(new UserId(UUID.fromString(claims.getSubject())), claims.getIssuedAt(),
                                    claims.getExpiration());

        when(tokenProperties.getSignInKey()).thenReturn(TokenHelper.createSigningKey());
        when(tokenPayloadFactory.createPayload(any(TokenType.class), any(Claims.class))).thenReturn(expectedPayload);


        // Act
        assertThrows(TokenException.class, () -> tokenService.parseToken(token, TokenType.ACCESS));

    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public TokenProperties tokenProperties() {
            var secretKey =
                "614E645267556B58703273357638792F413F4428472B4B6250655368566D597133743677397A244326452948404D635166546A576E5A7234753778214125442A";
            var expiration = Map.of(TokenType.REFRESH, Duration.ofSeconds(3600));
            return new TokenProperties(secretKey, expiration);
        }
    }
}
