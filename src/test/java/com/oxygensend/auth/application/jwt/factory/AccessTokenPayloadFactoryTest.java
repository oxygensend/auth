package com.oxygensend.auth.application.jwt.factory;

import com.oxygensend.auth.application.IdentityProvider;
import com.oxygensend.auth.application.jwt.payload.AccessTokenPayload;
import com.oxygensend.auth.application.jwt.payload.TokenPayload;
import com.oxygensend.auth.domain.User;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessTokenPayloadFactoryTest {

    @Mock
    private IdentityProvider identityProvider;

    @InjectMocks
    private AccessTokenPayloadFactory factory;


    @Test
    public void testCreateTokenPayload_ValidArguments() {
        // Arrange
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        Date iatDate = new Date();
        var user = User.builder()
                       .id(UUID.randomUUID())
                       .email("test@test.pl")
                       .roles(Set.of("ROLE_ADMIN"))
                       .build();

        when(identityProvider.getIdentity(eq(user))).thenReturn(user.email());

        // Act
        TokenPayload tokenPayload = factory.createToken(expDate, iatDate, user);

        // Assert
        assertNotNull(tokenPayload);
        assertTrue(tokenPayload instanceof AccessTokenPayload);

        AccessTokenPayload accessTokenPayload = (AccessTokenPayload) tokenPayload;
        assertEquals(iatDate, accessTokenPayload.iat());
        assertEquals(expDate, accessTokenPayload.exp());
        assertEquals(user.email(), accessTokenPayload.identity());
        assertEquals(user.id().toString(), accessTokenPayload.userId());
        assertEquals(user.roles(), accessTokenPayload.roles());
    }

    @Test
    public void testCreateTokenPayloadFromClaims_ValidClaims() {
        // Arrange
        Date expDate = new Date(System.currentTimeMillis() + 3600);
        Date iatDate = new Date();

        Claims claims = createClaims(iatDate, expDate, "John", "Doe", "john.doe@example.com");

        // Act
        TokenPayload tokenPayload = factory.createToken(claims);

        // Assert
        assertNotNull(tokenPayload);
        assertTrue(tokenPayload instanceof AccessTokenPayload);

        AccessTokenPayload accessTokenPayload = (AccessTokenPayload) tokenPayload;
        assertEquals(iatDate, accessTokenPayload.iat());
        assertEquals(expDate, accessTokenPayload.exp());
        assertEquals("john.doe@example.com", accessTokenPayload.identity());
        assertEquals("1", accessTokenPayload.userId());
        assertTrue(accessTokenPayload.roles().contains("ROLE_ADMIN"));
    }

    private Claims createClaims(Date iat, Date exp, String firstName, String lastName, String email) {

        Claims claims = mock(Claims.class);
        when(claims.getIssuedAt()).thenReturn(iat);
        when(claims.getExpiration()).thenReturn(exp);
        when(claims.getSubject()).thenReturn(email);
        when(claims.get("userId", String.class)).thenReturn("1");
        when(claims.get("roles")).thenReturn(List.of("ROLE_ADMIN"));
        when(claims.get("verified", Boolean.class)).thenReturn(true);

        return claims;
    }
}
