package com.oxygensend.auth.port.in.rest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxygensend.auth.application.GoogleOauthService;
import com.oxygensend.auth.application.GoogleUnauthenticatedException;
import com.oxygensend.auth.application.auth.AuthenticationTokensDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthControllerTest {

    private static final String GOOGLE_AUTH_URL =
        "https://accounts.google.com/oauth/authorize?client_id=test&redirect_uri=https://example.com/callback&response_type=code&scope=email%20profile";
    private static final String VALID_CODE = "valid-auth-code";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private GoogleOauthService googleOauthService;

    @InjectMocks
    private GoogleOAuthController googleOAuthController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(googleOAuthController)
            .setValidator(new LocalValidatorFactoryBean())
            .setControllerAdvice(new GoogleOAuthExceptionHandler())
            .build();
    }

    @Test
    void redirectToGoogle_shouldReturnRedirectViewWithGoogleAuthUrl() throws Exception {
        // Given
        when(googleOauthService.getAuthUrl()).thenReturn(GOOGLE_AUTH_URL);

        // When & Then
        mockMvc.perform(get("/v1/oauth2/google/auth-url"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(GOOGLE_AUTH_URL));
    }

    @Test
    void grantCode_withValidCode_shouldReturnAuthenticationTokens() throws Exception {
        // Given
        GoogleLoginRequest request = new GoogleLoginRequest(VALID_CODE);
        AuthenticationTokensDto tokens = new AuthenticationTokensDto(ACCESS_TOKEN, REFRESH_TOKEN);

        when(googleOauthService.authenticate(eq(VALID_CODE))).thenReturn(tokens);

        // When & Then
        mockMvc.perform(post("/v1/oauth2/google/code")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value(ACCESS_TOKEN))
            .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    void grantCode_withNullCode_shouldReturnBadRequest() throws Exception {
        // Given
        GoogleLoginRequest request = new GoogleLoginRequest(null);

        // When & Then
        mockMvc.perform(post("/v1/oauth2/google/code")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void grantCode_withBlankCode_shouldReturnBadRequest() throws Exception {
        // Given
        GoogleLoginRequest request = new GoogleLoginRequest("");

        // When & Then
        mockMvc.perform(post("/v1/oauth2/google/code")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void grantCode_whenServiceThrowsUnauthenticatedException_shouldPropagateException() throws Exception {
        // Given
        GoogleLoginRequest request = new GoogleLoginRequest(VALID_CODE);
        when(googleOauthService.authenticate(eq(VALID_CODE)))
            .thenThrow(new GoogleUnauthenticatedException(new RuntimeException("Google API error")));

        // When & Then
        mockMvc.perform(post("/v1/oauth2/google/code")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
}