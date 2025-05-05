package com.oxygensend.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.token.TokenService;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.payload.TokenPayload;
import com.oxygensend.auth.domain.model.token.payload.TokenPayloadFactoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TokenApplicationServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenPayloadFactoryProvider tokenPayloadFactoryProvider;


    @Mock
    private TokenSubject tokenSubject;

    @Mock
    private TokenPayload tokenPayload;

    private Map<TokenType, Duration> tokenExpirationMap;
    private TokenApplicationService tokenApplicationService;

    @BeforeEach
    void setUp() {
        tokenExpirationMap = new HashMap<>();
        tokenExpirationMap.put(TokenType.ACCESS, Duration.ofMinutes(15));
        tokenExpirationMap.put(TokenType.REFRESH, Duration.ofDays(7));
        tokenExpirationMap.put(TokenType.PASSWORD_RESET, Duration.ofHours(1));
        tokenExpirationMap.put(TokenType.EMAIL_VERIFICATION, Duration.ofHours(24));

        tokenApplicationService = new TokenApplicationService(
            tokenService,
            tokenExpirationMap,
            tokenPayloadFactoryProvider
        );
    }

    @Test
    void shouldCreateToken_whenValidSubjectAndTypeProvided() {
        // Given
        TokenType tokenType = TokenType.ACCESS;
        String expectedToken = "generated.token.string";

        when(tokenPayloadFactoryProvider.createPayload(
            eq(tokenType),
            any(Date.class),
            any(Date.class),
            eq(tokenSubject)
        )).thenReturn(tokenPayload);

        when(tokenService.createToken(tokenPayload)).thenReturn(expectedToken);

        // When
        String result = tokenApplicationService.createToken(tokenSubject, tokenType);

        // Then
        assertThat(result).isEqualTo(expectedToken);

        // Verify the dates are calculated correctly with expiration
        ArgumentCaptor<Date> expDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> issueDateCaptor = ArgumentCaptor.forClass(Date.class);

        verify(tokenPayloadFactoryProvider).createPayload(
            eq(tokenType),
            expDateCaptor.capture(),
            issueDateCaptor.capture(),
            eq(tokenSubject)
        );

        Date expDate = expDateCaptor.getValue();
        Date issueDate = issueDateCaptor.getValue();

        // The expiration time should be around 15 minutes (900000ms) after issue time
        long expectedDiffMillis = Duration.ofMinutes(15).toMillis();
        long actualDiffMillis = expDate.getTime() - issueDate.getTime();

        // Allow small timing differences due to test execution
        assertThat(actualDiffMillis).isCloseTo(expectedDiffMillis, within(1000L));

        verify(tokenService).createToken(tokenPayload);
    }

    @Test
    void shouldCreateTokenWithLongerExpiration_whenRefreshTokenTypeProvided() {
        // Given
        TokenType tokenType = TokenType.REFRESH;
        String expectedToken = "generated.refresh.token";

        when(tokenPayloadFactoryProvider.createPayload(
            eq(tokenType),
            any(Date.class),
            any(Date.class),
            eq(tokenSubject)
        )).thenReturn(tokenPayload);

        when(tokenService.createToken(tokenPayload)).thenReturn(expectedToken);

        // When
        String result = tokenApplicationService.createToken(tokenSubject, tokenType);

        // Then
        assertThat(result).isEqualTo(expectedToken);

        // Verify the dates are calculated correctly with expiration
        ArgumentCaptor<Date> expDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> issueDateCaptor = ArgumentCaptor.forClass(Date.class);

        verify(tokenPayloadFactoryProvider).createPayload(
            eq(tokenType),
            expDateCaptor.capture(),
            issueDateCaptor.capture(),
            eq(tokenSubject)
        );

        Date expDate = expDateCaptor.getValue();
        Date issueDate = issueDateCaptor.getValue();

        // The expiration time should be around 7 days after issue time
        long expectedDiffMillis = Duration.ofDays(7).toMillis();
        long actualDiffMillis = expDate.getTime() - issueDate.getTime();

        // Allow small timing differences due to test execution
        assertThat(actualDiffMillis).isCloseTo(expectedDiffMillis, within(1000L));
    }

    @Test
    void shouldThrowException_whenTokenTypeNotInExpirationMap() {
        // Given
        TokenType tokenType = TokenType.NOT_SPECIFIED;

        // The expiration map doesn't contain NOT_SPECIFIED token type

        // When & Then
        assertThatThrownBy(() -> tokenApplicationService.createToken(tokenSubject, tokenType))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token type not supported");

        verify(tokenService, never()).createToken(any());
    }

    @Test
    void shouldParseToken_whenValidTokenAndTypeProvided() {
        // Given
        String token = "valid.token.string";
        TokenType tokenType = TokenType.ACCESS;

        when(tokenService.parseToken(token, tokenType)).thenReturn(tokenPayload);

        // When
        TokenPayload result = tokenApplicationService.parseToken(token, tokenType);

        // Then
        assertThat(result).isEqualTo(tokenPayload);
        verify(tokenService).parseToken(token, tokenType);
    }
}
