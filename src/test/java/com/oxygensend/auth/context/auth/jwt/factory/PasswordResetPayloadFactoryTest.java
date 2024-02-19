package com.oxygensend.auth.context.auth.jwt.factory;

import com.oxygensend.auth.context.auth.jwt.payload.PasswordResetTokenPayload;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetPayloadFactoryTest {

    @InjectMocks
    private PasswordResetTokenPayloadFactory factory;


    @Test
    void testCreateTokenPayload() {
        // Arrange
        Date exp = new Date();
        Date iat = new Date();
        var user = mock(User.class);
        when(user.id()).thenReturn(UUID.randomUUID());


        // Act
        PasswordResetTokenPayload payload = (PasswordResetTokenPayload) factory.createToken(exp, iat, user);

        // Assert
        assertEquals(PasswordResetTokenPayload.class, payload.getClass());
        assertEquals(iat, payload.iat());
        assertEquals(exp, payload.exp());
        assertEquals(user.id().toString(), payload.userId());
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
        PasswordResetTokenPayload payload = (PasswordResetTokenPayload) factory.createToken(claims);

        // Assert
        assertEquals(PasswordResetTokenPayload.class, payload.getClass());
        assertEquals(issuedAt, payload.iat());
        assertEquals(expiration, payload.exp());
        assertEquals(claims.getSubject(), payload.userId());
    }
}
