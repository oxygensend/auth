package com.oxygensend.auth.port.out.http;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class GoogleOAuthHttpApiTest {

    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final String REDIRECT_URI = "https://example.com/callback";
    private static final String INVALID_CODE = "invalid-code";
    private static final String INVALID_ACCESS_TOKEN = "invalid-token";

    private GoogleOAuthHttpApi googleOAuthHttpApi;

    @BeforeEach
    void setUp() {
        googleOAuthHttpApi = new GoogleOAuthHttpApi();
    }

    @Test
    void getAccessToken_withInvalidCode_shouldThrowException() {
        // When & Then
        assertThatThrownBy(
            () -> googleOAuthHttpApi.getAccessToken(INVALID_CODE, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI))
            .isInstanceOf(IOException.class);
    }

    @Test
    void getUserInfo_withInvalidAccessToken_shouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> googleOAuthHttpApi.getUserInfo(INVALID_ACCESS_TOKEN))
            .isInstanceOf(Exception.class);
    }
}