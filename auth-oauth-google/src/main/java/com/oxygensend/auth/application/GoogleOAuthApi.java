package com.oxygensend.auth.application;

import com.google.api.services.oauth2.model.Userinfo;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleOAuthApi {

    String getAccessToken(String code, String clientId, String clientSecret, String redirectUri)
        throws GeneralSecurityException, IOException;

    Userinfo getUserInfo(String accessToken) throws GeneralSecurityException, IOException;
}
