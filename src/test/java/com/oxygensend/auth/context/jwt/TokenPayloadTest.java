package com.oxygensend.auth.context.jwt;

import com.oxygensend.auth.context.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.context.jwt.payload.RefreshTokenPayload;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TokenPayloadTest {


    @Test
    public void test_AccessTokenPayloadToClaims() {
        // Arrange
        var accessTokenPayload = new AccessTokenPayload("test@tes.com", "1", Set.of("ROLE_ADMIN"), new Date(), new Date(), true);

        // Act
        var claims = accessTokenPayload.toClaims();

        // Assert
        assertInstanceOf(Claims.class, accessTokenPayload.toClaims());
        assertEquals(claims.getExpiration().toString(), accessTokenPayload.exp().toString());
        assertEquals(claims.getSubject(), accessTokenPayload.identity());
        assertEquals(claims.getIssuedAt().toString(), accessTokenPayload.iat().toString());
        assertEquals(claims.get("type"), accessTokenPayload.type());
        assertEquals(claims.get("userId"), accessTokenPayload.userId());
        assertEquals(claims.get("roles"), accessTokenPayload.roles());
    }

    @Test
    public void test_RefreshTokenPayloadToClaims() {
        // Arrange
        var accessTokenPayload = new RefreshTokenPayload(UUID.randomUUID(), new Date(), new Date());

        // Act
        var claims = accessTokenPayload.toClaims();

        // Assert
        assertInstanceOf(Claims.class, accessTokenPayload.toClaims());
        assertEquals(claims.getExpiration().toString(), accessTokenPayload.exp().toString());
        assertEquals(UUID.fromString(claims.getSubject()), accessTokenPayload.sessionId());
        assertEquals(claims.getIssuedAt().toString(), accessTokenPayload.iat().toString());
        assertEquals(claims.get("type"), accessTokenPayload.type());
    }
}
