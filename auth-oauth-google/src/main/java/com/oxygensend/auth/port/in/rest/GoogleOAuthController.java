package com.oxygensend.auth.port.in.rest;

import com.oxygensend.auth.application.GoogleOauthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/v1/oauth2/google")
@RestController
public class GoogleOAuthController {

    private final GoogleOauthService googleOauthService;

    public GoogleOAuthController(GoogleOauthService googleOauthService) {
        this.googleOauthService = googleOauthService;
    }


    @GetMapping("/auth-url")
    public RedirectView redirectToGoogle() {
        return new RedirectView(googleOauthService.getAuthUrl());
    }


    @PostMapping("/code")
    public AuthenticationResponse grantCode(@RequestBody @Validated GoogleLoginRequest request) throws Exception {
       var tokens =  googleOauthService.authenticate(request.code());
       return new AuthenticationResponse(tokens.accessToken(), tokens.refreshToken());
    }
}
