package com.oxygensend.auth.port.out.http.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.oxygensend.auth.application.GoogleOAuthApi;
import com.oxygensend.auth.application.GoogleOAuthData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
class GoogleOAuthHttpApi implements GoogleOAuthApi {

    @Override
    public String getAccessToken(String code, String clientId, String clientSecret, String redirectUri)
        throws GeneralSecurityException, IOException {
        return new GoogleAuthorizationCodeTokenRequest(GoogleNetHttpTransport.newTrustedTransport(),
                                                       GsonFactory.getDefaultInstance(),
                                                       clientId,
                                                       clientSecret,
                                                       code,
                                                       redirectUri)
            .execute().getAccessToken();
    }

    @Override
    public Userinfo getUserInfo(String accessToken) throws GeneralSecurityException, IOException {
        Oauth2 oauth2 = new Oauth2.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            new GoogleCredential().setAccessToken(accessToken))
            .build();

        return oauth2.userinfo().get().execute();
    }
}
