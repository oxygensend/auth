package com.oxygensend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.oauth2.model.Userinfo;
import com.oxygensend.auth.application.auth.AuthService;
import com.oxygensend.auth.application.auth.AuthenticationTokensDto;
import com.oxygensend.auth.application.business_callback.BusinessRequestDto;
import com.oxygensend.auth.application.business_callback.BusinessService;
import com.oxygensend.auth.domain.model.OauthUser;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GoogleOAuthServiceTest {

    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final String REDIRECT_URI = "https://example.com/callback";
    private static final String AUTH_URL_TEMPLATE =
        "https://accounts.google.com/oauth/authorize?client_id={clientId}&redirect_uri={redirectUri}&response_type=code&scope=email%20profile";
    private static final String BUSINESS_CALLBACK_URL = "https://business.example.com/callback";
    private static final String CODE = "auth-code";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String EMAIL = "test@example.com";
    private static final String GIVEN_NAME = "John";
    private static final String FAMILY_NAME = "Doe";
    private static final String GOOGLE_ID = "google-123";
    private static final Set<Role> DEFAULT_ROLES = Set.of(new Role("USER"));
    @Mock
    private GoogleOAuthData oAuthData;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;
    @Mock
    private GoogleOAuthApi googleOAuthApi;
    @Mock
    private BusinessService businessService;
    private GoogleOauthService googleOauthService;

    @BeforeEach
    void setUp() {
        googleOauthService =
            new GoogleOauthService(oAuthData, userRepository, authService, googleOAuthApi, businessService);

        // Common mocks
        lenient().when(oAuthData.clientId()).thenReturn(CLIENT_ID);
        lenient().when(oAuthData.clientSecret()).thenReturn(CLIENT_SECRET);
        lenient().when(oAuthData.redirectUri()).thenReturn(REDIRECT_URI);
        lenient().when(oAuthData.authUrl()).thenReturn(AUTH_URL_TEMPLATE);
        lenient().when(oAuthData.defaultRoles()).thenReturn(DEFAULT_ROLES);
    }

    @Test
    void getAuthUrl_shouldReturnProperGoogleAuthenticationUrl() {
        // When
        String result = googleOauthService.getAuthUrl();

        // Then
        String expectedUrl = "https://accounts.google.com/oauth/authorize?client_id=" + CLIENT_ID +
            "&redirect_uri=" + REDIRECT_URI + "&response_type=code&scope=email%20profile";
        assertThat(result).isEqualTo(expectedUrl);
    }

    @Test
    void authenticate_withNewUserNoBusinessCallback_shouldRegisterNewUserAndAuthenticateItInGoogle() throws Exception {
        // Given
        when(oAuthData.businessCallbackUrl()).thenReturn(null);

        Userinfo userInfo = createUserInfo();
        UserId userId = new UserId(UUID.randomUUID());
        AuthenticationTokensDto authTokens = new AuthenticationTokensDto("jwt-token", "refresh-token");

        when(googleOAuthApi.getAccessToken(CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)).thenReturn(ACCESS_TOKEN);
        when(googleOAuthApi.getUserInfo(ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());
        when(userRepository.nextIdentity()).thenReturn(userId);
        when(authService.authenticate(any(User.class))).thenReturn(authTokens);

        // When
        AuthenticationTokensDto result = googleOauthService.authenticate(CODE);

        // Then
        assertThat(result).isEqualTo(authTokens);

        // Verify user was saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser).isInstanceOf(OauthUser.class);
        assertThat(savedUser.id()).isEqualTo(userId);
        assertThat(savedUser.credentials().email().address()).isEqualTo(EMAIL);
        assertThat(savedUser.credentials().username().value()).isEqualTo("test");
        assertThat(savedUser.googleId().value()).isEqualTo(GOOGLE_ID);
        assertThat(savedUser.businessId()).isNull();

        // Verify business service was NOT called
        verify(businessService, never()).sendData(any(BusinessRequestDto.class));
        verify(authService).authenticate(savedUser);
    }

    @Test
    void authenticate_withNewUserWithBusinessServiceCallback_shouldRegisterNewUserInBusinessServiceAndAuthenticateItInGoogle()
        throws Exception {
        // Given
        when(oAuthData.businessCallbackUrl()).thenReturn(BUSINESS_CALLBACK_URL);

        Userinfo userInfo = createUserInfo();
        UserId userId = new UserId(UUID.randomUUID());
        BusinessId businessId = new BusinessId("business-456");
        AuthenticationTokensDto authTokens = new AuthenticationTokensDto("jwt-token", "refresh-token");

        when(googleOAuthApi.getAccessToken(CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)).thenReturn(ACCESS_TOKEN);
        when(googleOAuthApi.getUserInfo(ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());
        when(userRepository.nextIdentity()).thenReturn(userId);
        when(businessService.sendData(any(BusinessRequestDto.class))).thenReturn(businessId);
        when(authService.authenticate(any(User.class))).thenReturn(authTokens);

        // When
        AuthenticationTokensDto result = googleOauthService.authenticate(CODE);

        // Then
        assertThat(result).isEqualTo(authTokens);

        // Verify business service was called with correct data
        ArgumentCaptor<BusinessRequestDto> businessRequestCaptor = ArgumentCaptor.forClass(BusinessRequestDto.class);
        verify(businessService).sendData(businessRequestCaptor.capture());

        BusinessRequestDto businessRequest = businessRequestCaptor.getValue();
        assertThat(businessRequest.userId()).isEqualTo(userId);
        assertThat(businessRequest.firstName()).isEqualTo(GIVEN_NAME);
        assertThat(businessRequest.lastName()).isEqualTo(FAMILY_NAME);
        assertThat(businessRequest.email().address()).isEqualTo(EMAIL);

        // Verify user was saved with business ID
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser).isInstanceOf(OauthUser.class);
        assertThat(savedUser.id()).isEqualTo(userId);
        assertThat(savedUser.credentials().email().address()).isEqualTo(EMAIL);
        assertThat(savedUser.credentials().username().value()).isEqualTo("test");
        assertThat(savedUser.googleId().value()).isEqualTo(GOOGLE_ID);
        assertThat(savedUser.businessId()).isEqualTo(businessId);

        verify(authService).authenticate(savedUser);
    }

    @Test
    void authenticate_withExistingUser_shouldAuthenticateTheUser() throws Exception {
        // Given
        Userinfo userInfo = createUserInfo();
        User existingUser = mock(User.class);
        AuthenticationTokensDto authTokens = new AuthenticationTokensDto("jwt-token", "refresh-token");

        when(googleOAuthApi.getAccessToken(CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)).thenReturn(ACCESS_TOKEN);
        when(googleOAuthApi.getUserInfo(ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(existingUser));
        when(authService.authenticate(existingUser)).thenReturn(authTokens);

        // When
        AuthenticationTokensDto result = googleOauthService.authenticate(CODE);

        // Then
        assertThat(result).isEqualTo(authTokens);

        // Verify existing user was authenticated
        verify(authService).authenticate(existingUser);

        // Verify no new user was created
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).nextIdentity();

        // Verify business service was NOT called
        verify(businessService, never()).sendData(any(BusinessRequestDto.class));
    }

    @Test
    void authenticate_withEmptyBusinessCallbackUrl_shouldTreatAsNoBusinessCallback() throws Exception {
        // Given
        when(oAuthData.businessCallbackUrl()).thenReturn("");

        Userinfo userInfo = createUserInfo();
        UserId userId = new UserId(UUID.randomUUID());
        AuthenticationTokensDto authTokens = new AuthenticationTokensDto("jwt-token", "refresh-token");

        when(googleOAuthApi.getAccessToken(CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)).thenReturn(ACCESS_TOKEN);
        when(googleOAuthApi.getUserInfo(ACCESS_TOKEN)).thenReturn(userInfo);
        when(userRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());
        when(userRepository.nextIdentity()).thenReturn(userId);
        when(authService.authenticate(any(User.class))).thenReturn(authTokens);

        // When
        AuthenticationTokensDto result = googleOauthService.authenticate(CODE);

        // Then
        assertThat(result).isEqualTo(authTokens);

        // Verify user was saved without business ID
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.businessId()).isNull();

        // Verify business service was NOT called
        verify(businessService, never()).sendData(any(BusinessRequestDto.class));
    }

    @Test
    void authenticate_whenGoogleApiThrowsException_shouldThrowUnauthenticatedException() throws Exception {
        // Given
        Exception googleApiException = new RuntimeException("Google API error");
        when(googleOAuthApi.getAccessToken(CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI)).thenThrow(googleApiException);

        // When & Then
        assertThatThrownBy(() -> googleOauthService.authenticate(CODE))
            .isInstanceOf(UnauthenticatedException.class)
            .hasCause(googleApiException);

        // Verify no user operations were performed
        verify(userRepository, never()).findByEmail(any(EmailAddress.class));
        verify(userRepository, never()).save(any(User.class));
        verify(authService, never()).authenticate(any(User.class));
        verify(businessService, never()).sendData(any(BusinessRequestDto.class));
    }

    private Userinfo createUserInfo() {
        Userinfo userInfo = new Userinfo();
        userInfo.setEmail(EMAIL);
        userInfo.setGivenName(GIVEN_NAME);
        userInfo.setFamilyName(FAMILY_NAME);
        userInfo.setId(GOOGLE_ID);
        return userInfo;
    }
}