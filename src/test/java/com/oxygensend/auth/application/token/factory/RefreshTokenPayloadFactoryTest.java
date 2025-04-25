package com.oxygensend.auth.application.token.factory;

import com.oxygensend.auth.domain.model.crypto.factory.RefreshTokenPayloadFactory;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import com.oxygensend.auth.domain.model.identity.User;
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
public class RefreshTokenPayloadFactoryTest {

    @InjectMocks
    private RefreshTokenPayloadFactory factory;


    @Test
    public void testCreateTokenPayload() {
        // Arrange
        Date exp = new Date();
        Date iat = new Date();
        var user = mock(User.class);
        when(user.id()).thenReturn(UUID.randomUUID());


        // Act
        RefreshTokenPayload payload = (RefreshTokenPayload) factory.createToken(exp, iat, user);

        // Assert
        assertEquals(RefreshTokenPayload.class, payload.getClass());
        assertEquals(iat, payload.iat());
        assertEquals(exp, payload.exp());
        assertEquals(user.id(), payload.userId());
    }

    @Test
    public void testCreateTokenPayloadFromClaims() {
        // Arrange
        Date issuedAt = new Date();
        Date expiration = new Date();
        Claims claims = mock(Claims.class);
        when(claims.getIssuedAt()).thenReturn(issuedAt);
        when(claims.getExpiration()).thenReturn(expiration);
        when(claims.getSubject()).thenReturn(UUID.randomUUID().toString());


        // Act
        RefreshTokenPayload payload = (RefreshTokenPayload) factory.createToken(claims);

        // Assert
        assertEquals(RefreshTokenPayload.class, payload.getClass());
        assertEquals(issuedAt, payload.iat());
        assertEquals(expiration, payload.exp());
        assertEquals(claims.getSubject(), payload.userId().toString());
    }
}
