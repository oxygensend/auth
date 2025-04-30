package com.oxygensend.auth.domain.model.token.payload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.token.PasswordResetTokenSubject;
import com.oxygensend.auth.helper.UserMother;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PasswordResetPayloadFactoryTest {

    @InjectMocks
    private PasswordResetTokenPayloadFactory factory;


    @Test
    void testCreateTokenPayload() {
        // Arrange
        Date exp = new Date();
        Date iat = new Date();
        var user = UserMother.getRandom();


        // Act
        PasswordResetTokenPayload payload =
            (PasswordResetTokenPayload) factory.createPayload(exp, iat, new PasswordResetTokenSubject(user.id()));

        // Assert
        assertEquals(PasswordResetTokenPayload.class, payload.getClass());
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
        PasswordResetTokenPayload payload = (PasswordResetTokenPayload) factory.createPayload(claims);

        // Assert
        assertEquals(PasswordResetTokenPayload.class, payload.getClass());
        assertEquals(issuedAt, payload.iat());
        assertEquals(expiration, payload.exp());
        assertEquals(claims.getSubject(), payload.userId().toString());
    }
}
