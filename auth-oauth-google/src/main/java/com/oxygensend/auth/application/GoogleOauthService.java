package com.oxygensend.auth.application;

import com.google.api.services.oauth2.model.Userinfo;
import com.google.common.base.Strings;
import com.oxygensend.auth.application.auth.AuthService;
import com.oxygensend.auth.application.auth.AuthenticationTokensDto;
import com.oxygensend.auth.application.business_callback.BusinessRequestDto;
import com.oxygensend.auth.application.business_callback.BusinessService;
import com.oxygensend.auth.domain.model.OauthUser;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.GoogleId;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserRepository;
import com.oxygensend.auth.domain.model.identity.Username;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoogleOauthService {


    private final GoogleOAuthData oAuthData;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final GoogleOAuthApi googleOAuthApi;
    private final BusinessService businessService;


    public GoogleOauthService(GoogleOAuthData googleOAuthConnection, UserRepository userRepository,
                              AuthService authService,
                              GoogleOAuthApi googleOAuthApi, BusinessService businessService) {
        this.oAuthData = googleOAuthConnection;
        this.userRepository = userRepository;
        this.authService = authService;
        this.businessService = businessService;
        this.googleOAuthApi = googleOAuthApi;
    }

    public String getAuthUrl() {
        return oAuthData.authUrl()
            .replace("{clientId}", oAuthData.clientId())
            .replace("{redirectUri}", oAuthData.redirectUri());
    }

    public AuthenticationTokensDto authenticate(String code) {
        Userinfo userInfo = getGoogleUserInfo(code);

        EmailAddress emailAddress = new EmailAddress(userInfo.getEmail());
        Optional<User> user = userRepository.findByEmail(emailAddress);
        if (user.isEmpty()) {
            BusinessId businessId = null;
            UserId userId = userRepository.nextIdentity();
            if (isBusinessCallbackDefined()) { // synchronize newly registered user with business layer
                businessId = businessService.sendData(new BusinessRequestDto(userId,
                                                                             userInfo.getGivenName(),
                                                                             userInfo.getFamilyName(),
                                                                             emailAddress));
            }

            Username username = new Username(userInfo.getEmail().split("@")[0]);
            OauthUser newUser = OauthUser.registerUser(userId,
                                                 oAuthData.defaultRoles(),
                                                 new Credentials(emailAddress, username),
                                                 businessId,
                                                 new GoogleId(userInfo.getId()));
            userRepository.save(newUser);
            return authService.authenticate(newUser);
        }

        return authService.authenticate(user.get());
    }

    private Userinfo getGoogleUserInfo(String code) {
        try {
            String accessToken = googleOAuthApi.getAccessToken(code, oAuthData.clientId(), oAuthData.clientSecret(),
                                                               oAuthData.redirectUri());
            return googleOAuthApi.getUserInfo(accessToken);
        } catch (Exception exception) {
            throw new GoogleUnauthenticatedException(exception);
        }

    }

    private boolean isBusinessCallbackDefined() {
        return !Strings.isNullOrEmpty(oAuthData.businessCallbackUrl());
    }

}
