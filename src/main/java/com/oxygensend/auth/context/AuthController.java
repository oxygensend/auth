package com.oxygensend.auth.context;


import com.oxygensend.auth.context.request.AuthenticationRequest;
import com.oxygensend.auth.context.request.RefreshTokenRequest;
import com.oxygensend.auth.context.request.RegisterRequest;
import com.oxygensend.auth.context.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@RequestBody @Validated RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/access_token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse authenticate(@RequestBody @Validated AuthenticationRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/refresh_token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

}
