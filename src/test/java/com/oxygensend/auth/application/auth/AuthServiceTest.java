package com.oxygensend.auth.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oxygensend.auth.application.settings.CurrentAccountActivationType;
import com.oxygensend.auth.application.settings.LoginDto;
import com.oxygensend.auth.application.settings.LoginProvider;
import com.oxygensend.auth.application.settings.LoginType;
import com.oxygensend.auth.application.token.TokenApplicationService;
import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.domain.model.identity.AuthenticationService;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.PasswordService;
import com.oxygensend.auth.domain.model.identity.RegistrationService;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserDescriptor;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserMother;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.domain.model.session.Session;
import com.oxygensend.auth.domain.model.session.SessionId;
import com.oxygensend.auth.domain.model.session.SessionManager;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.RefreshTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.token.exception.TokenException;
import com.oxygensend.auth.domain.model.token.payload.RefreshTokenPayload;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenApplicationService tokenApplicationService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private PasswordService passwordService;
    @Mock
    private LoginProvider loginProvider;
    @Mock
    private SessionManager sessionManager;
    @Mock
    private CurrentAccountActivationType currentAccountActivationType;

    @InjectMocks
    private AuthService authService;


    @Test
    void shouldRegisterUser_withValidCommand() {
        // given
        RegisterCommand command = registerCommand();
        AccountActivationType activationType = AccountActivationType.VERIFY_EMAIL;
        when(currentAccountActivationType.get()).thenReturn(activationType);

        User user = mockUser();
        when(passwordService.encode(command.rawPassword())).thenReturn("encoded_password");
        when(registrationService.registerUser(any(), eq(command.roles()), eq(command.businessId()), eq(activationType)))
            .thenReturn(user);
        givenSessionAndTokens(user.id());

        // when
        Pair<UserId, AuthenticationTokensDto> result = authService.register(command);

        // then
        assertThat(result.getLeft()).isEqualTo(user.id());
        assertTokens(result.getRight());
    }

    @Test
    void shouldAuthenticate_withEmail() {
        // given
        String login = "user@example.com";
        LoginDto loginDto = new LoginDto(login, LoginType.EMAIL);
        when(loginProvider.get(login)).thenReturn(loginDto);
        UserDescriptor descriptor = userDescriptor(login, "testuser");
        when(authenticationService.authenticateWithEmail(new EmailAddress(login), "password123"))
            .thenReturn(descriptor);
        givenSessionAndTokens(descriptor.userId());

        // when
        AuthenticationTokensDto result = authService.authenticate(login, "password123");

        // then
        assertTokens(result);
    }

    @Test
    void shouldAuthenticate_withUsername() {
        // given
        String login = "testuser";
        LoginDto loginDto = new LoginDto(login, LoginType.USERNAME);
        when(loginProvider.get(login)).thenReturn(loginDto);
        UserDescriptor descriptor = userDescriptor("user@example.com", login);
        when(authenticationService.authenticateWithUsername(new Username(login), "password123"))
            .thenReturn(descriptor);
        givenSessionAndTokens(descriptor.userId());

        // when
        AuthenticationTokensDto result = authService.authenticate(login, "password123");

        // then
        assertTokens(result);
    }

    @Test
    void shouldRefreshToken_withValidToken() {
        // given
        String token = "refresh_token";
        UserId userId = UserMother.userId();
        RefreshTokenPayload payload = new RefreshTokenPayload(userId, new Date(), futureDate());
        Session session = mockSession(userId);
        when(tokenApplicationService.parseToken(token, TokenType.REFRESH)).thenReturn(payload);
        when(sessionManager.currentSession(userId)).thenReturn(session);
        when(authenticationService.revalidateUser(userId)).thenReturn(userDescriptor("user@example.com", "testuser"));
        givenTokens();

        // when
        AuthenticationTokensDto result = authService.refreshToken(token);

        // then
        assertTokens(result);
    }

    @Test
    void shouldThrowException_whenTokenExpired() {
        // given
        String token = "expired_token";
        UserId userId = UserMother.userId();
        RefreshTokenPayload expired = new RefreshTokenPayload(userId, new Date(), pastDate());
        when(tokenApplicationService.parseToken(token, TokenType.REFRESH)).thenReturn(expired);

        // when + then
        assertThatThrownBy(() -> authService.refreshToken(token))
            .isInstanceOf(TokenException.class)
            .hasMessageContaining("Token is expired");
        verifyNoMoreInteractions(sessionManager, authenticationService);
    }

    // ===== Helpers =====

    private RegisterCommand registerCommand() {
        return new RegisterCommand(
            new EmailAddress("user@example.com"),
            new Username("testuser"),
            "password123",
            Set.of(new Role("USER")),
            new BusinessId("business123")
        );
    }

    private User mockUser() {
        User user = mock(User.class);
        when(user.id()).thenReturn(new UserId(UUID.randomUUID()));
        when(user.businessId()).thenReturn(new BusinessId("business123"));
        when(user.username()).thenReturn(new Username("testuser"));
        when(user.email()).thenReturn(new EmailAddress("user@example.com"));
        when(user.roles()).thenReturn(Set.of(new Role("USER")));
        when(user.isVerified()).thenReturn(false);
        return user;
    }

    private void givenSessionAndTokens(UserId userId) {
        when(sessionManager.startSession(userId)).thenReturn(new SessionId(UUID.randomUUID()));
        givenTokens();
    }

    private void givenTokens() {
        when(tokenApplicationService.createToken(any(RefreshTokenSubject.class), eq(TokenType.REFRESH)))
            .thenReturn("refresh_token");
        when(tokenApplicationService.createToken(any(AccessTokenSubject.class), eq(TokenType.ACCESS)))
            .thenReturn("access_token");
    }

    private void assertTokens(AuthenticationTokensDto dto) {
        assertThat(dto.accessToken()).isEqualTo("access_token");
        assertThat(dto.refreshToken()).isEqualTo("refresh_token");
    }

    private UserDescriptor userDescriptor(String email, String username) {
        return new UserDescriptor(
            new UserId(UUID.randomUUID()),
            new Username(username),
            new EmailAddress(email),
            new BusinessId("business123"),
            Set.of(new Role("USER")),
            true
        );
    }

    private Session mockSession(UserId userId) {
        Session session = mock(Session.class);
        when(session.userId()).thenReturn(userId);
        return session;
    }

    private Date futureDate() {
        return new Date(System.currentTimeMillis() + 3600_000);
    }

    private Date pastDate() {
        return new Date(System.currentTimeMillis() - 3600_000);
    }
}
