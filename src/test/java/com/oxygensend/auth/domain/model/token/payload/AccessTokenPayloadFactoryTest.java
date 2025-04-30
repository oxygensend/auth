package com.oxygensend.auth.domain.model.token.payload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.helper.UserMother;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AccessTokenPayloadFactoryTest {

    @InjectMocks
    private AccessTokenPayloadFactory factory;


    @Test
    public void testCreateTokenPayload_ValidArguments() {
        // Arrange
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        Date iatDate = new Date();
        var user = UserMother.getRandom();

        var subject = new AccessTokenSubject(user.id(),
                                             user.businessId(),
                                             user.roles(),
                                             user.isVerified(),
                                             user.username(),
                                             user.email());
        // Act
        TokenPayload tokenPayload = factory.createPayload(expDate, iatDate, subject);

        // Assert
        assertNotNull(tokenPayload);
        assertInstanceOf(AccessTokenPayload.class, tokenPayload);

        AccessTokenPayload accessTokenPayload = (AccessTokenPayload) tokenPayload;
        assertEquals(iatDate, accessTokenPayload.iat());
        assertEquals(expDate, accessTokenPayload.exp());
        assertEquals(user.username(), accessTokenPayload.username());
        assertEquals(user.email(), accessTokenPayload.email());
        assertEquals(user.businessId(), accessTokenPayload.businessId());
        assertEquals(user.id(), accessTokenPayload.userId());
        assertEquals(user.roles(), accessTokenPayload.roles());
    }

    @Test
    public void testCreateTokenPayloadFromClaims_ValidClaims() {
        // Arrange
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        Date iatDate = new Date();

        var userId = new UserId(UUID.randomUUID());

        Claims claims = createClaims(iatDate, expDate, userId.value(), "john.doe", "john.doe@example.com");

        // Act
        TokenPayload tokenPayload = factory.createPayload(claims);

        // Assert
        assertNotNull(tokenPayload);
        assertInstanceOf(AccessTokenPayload.class, tokenPayload);

        AccessTokenPayload accessTokenPayload = (AccessTokenPayload) tokenPayload;
        assertEquals(iatDate, accessTokenPayload.iat());
        assertEquals(expDate, accessTokenPayload.exp());
        assertEquals(new EmailAddress("john.doe@example.com"), accessTokenPayload.email());
        assertEquals(new Username("john.doe"), accessTokenPayload.username());
        assertEquals(userId, accessTokenPayload.userId());
        assertEquals(accessTokenPayload.roles().contains(new Role("ROLE_ADMIN")), true);
        assertTrue(accessTokenPayload.verified());
    }

    private Claims createClaims(Date iat, Date exp, UUID userId, String username, String email) {

        Claims claims = mock(Claims.class);
        when(claims.getIssuedAt()).thenReturn(iat);
        when(claims.getExpiration()).thenReturn(exp);
        when(claims.getSubject()).thenReturn(email);
        when(claims.get("userId", String.class)).thenReturn(userId.toString());
        when(claims.get("roles")).thenReturn(List.of("ROLE_ADMIN"));
        when(claims.get("verified", Boolean.class)).thenReturn(true);
        when(claims.get("businessId", String.class)).thenReturn("1234");
        when(claims.get("username", String.class)).thenReturn(username);

        return claims;
    }
}
