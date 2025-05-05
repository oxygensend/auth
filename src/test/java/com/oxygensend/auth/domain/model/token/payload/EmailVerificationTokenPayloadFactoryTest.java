package com.oxygensend.auth.domain.model.token.payload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.token.EmailVerificationTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.UserMother;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EmailVerificationTokenPayloadFactoryTest {

    @InjectMocks
    private EmailVerificationTokenPayloadFactory factory;

    @Test
    void testCreateTokenPayload() {
        // Arrange
        Date exp = new Date();
        Date iat = new Date();
        var user = UserMother.getRandom();

        // Act
        EmailVerificationTokenPayload payload =
            (EmailVerificationTokenPayload) factory.createPayload(exp, iat, new EmailVerificationTokenSubject(user.id()));

        // Assert
        assertEquals(EmailVerificationTokenPayload.class, payload.getClass());
        assertEquals(TokenType.EMAIL_VERIFICATION, payload.type());
        assertEquals(iat, payload.iat());
        assertEquals(exp, payload.exp());
        assertEquals(user.id(), payload.userId());
    }

    @Test
    void testCreateTokenPayloadFromClaims() {
        // Arrange
        Date issuedAt = new Date();
        Date expiration = new Date();
        Claims claims = mock(Claims.class);
        when(claims.getIssuedAt()).thenReturn(issuedAt);
        when(claims.getExpiration()).thenReturn(expiration);
        when(claims.getSubject()).thenReturn(UUID.randomUUID().toString());

        // Act
        EmailVerificationTokenPayload payload = (EmailVerificationTokenPayload) factory.createPayload(claims);

        // Assert
        assertEquals(EmailVerificationTokenPayload.class, payload.getClass());
        assertEquals(TokenType.EMAIL_VERIFICATION, payload.type());
        assertEquals(issuedAt, payload.iat());
        assertEquals(expiration, payload.exp());
        assertEquals(claims.getSubject(), payload.userId().toString());
    }

    @Test
    void testGetType() {
        // Act
        TokenType type = factory.getType();

        // Assert
        assertEquals(TokenType.EMAIL_VERIFICATION, type);
    }

    @Test
    void testCreatePayloadWithInvalidSubject() {
        // Arrange
        Date exp = new Date();
        Date iat = new Date();
        TokenSubject invalidSubject = mock(TokenSubject.class);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createPayload(exp, iat, invalidSubject)
        );
    }
}
