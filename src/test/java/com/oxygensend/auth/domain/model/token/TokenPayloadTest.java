package com.oxygensend.auth.domain.model.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.token.payload.AccessTokenPayload;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import com.oxygensend.auth.helper.UserMother;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

public class TokenPayloadTest {


    @Test
    public void test_AccessTokenPayloadToClaims() {
        // Arrange
        var accessTokenPayload = new AccessTokenPayload(new Username("UserName"),
                                                        UserMother.getRandomUserId(),
                                                        Set.of(new Role("ROLE_ADMIN")),
                                                        new Date(),
                                                        new Date(),
                                                        true,
                                                        new BusinessId("1234"),
                                                        new EmailAddress("test@test.com"));

        // Act
        var claims = accessTokenPayload.toClaims();

        // Assert
        assertInstanceOf(Claims.class, accessTokenPayload.toClaims());
        assertEquals(claims.getExpiration().toString(), accessTokenPayload.exp().toString());
        assertEquals(claims.getSubject(), accessTokenPayload.email().toString());
        assertEquals(claims.getIssuedAt().toString(), accessTokenPayload.iat().toString());
        assertEquals(claims.get("type"), accessTokenPayload.type());
        assertEquals(claims.get("userId"), accessTokenPayload.userId().value());
        assertEquals(claims.get("roles"), accessTokenPayload.roles().stream().map(Role::value).toList());
        assertEquals(claims.get("verified"), accessTokenPayload.verified());
        assertEquals(claims.get("businessId"), accessTokenPayload.businessId().toString());
        assertEquals(claims.get("username"), accessTokenPayload.username().toString());
    }

    @Test
    public void test_RefreshTokenPayloadToClaims() {
        // Arrange
        var accessTokenPayload = new RefreshTokenPayload(UserMother.getRandomUserId(), new Date(), new Date());

        // Act
        var claims = accessTokenPayload.toClaims();

        // Assert
        assertInstanceOf(Claims.class, accessTokenPayload.toClaims());
        assertEquals(claims.getExpiration().toString(), accessTokenPayload.exp().toString());
        assertEquals(claims.getSubject(), accessTokenPayload.userId().toString());
        assertEquals(claims.getIssuedAt().toString(), accessTokenPayload.iat().toString());
        assertEquals(claims.get("type"), accessTokenPayload.type());
    }
}
