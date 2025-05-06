package com.oxygensend.auth.port.adapter.in.rest.resources.auth;


import com.oxygensend.auth.application.auth.AuthService;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.response.AuthenticationResponse;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.response.RegisterResponse;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.response.ValidationResponse;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.AuthenticationRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.RegisterRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = SwaggerConstants.AUTH_NAME, description = SwaggerConstants.AUTH_DESCRIPTION)
@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = SwaggerConstants.REGISTER_API_DESCRIPTION)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponse register(@RequestBody @Validated RegisterRequest request) {
        var userIdAndTokens = authService.register(request.toCommand());
        return new RegisterResponse(userIdAndTokens.getKey().value(),
                                    userIdAndTokens.getValue().accessToken(),
                                    userIdAndTokens.getValue().refreshToken());
    }

    @Operation(summary = SwaggerConstants.AUTHENTICATE_API_DESCRIPTION)
    @PostMapping("/access_token")
    @ResponseStatus(HttpStatus.OK)
    AuthenticationResponse authenticate(@RequestBody @Validated AuthenticationRequest request) {
        var tokens =  authService.authenticate(request.login(), request.password());
        return new AuthenticationResponse(tokens.accessToken(), tokens.refreshToken());
    }

    @Operation(summary = SwaggerConstants.REFRESH_TOKEN_API_DESCRIPTION)
    @PostMapping("/refresh_token")
    @ResponseStatus(HttpStatus.OK)
    AuthenticationResponse refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        var tokens = authService.refreshToken(request.token());
        return new AuthenticationResponse(tokens.accessToken(), tokens.refreshToken());
    }

    @Operation(summary = SwaggerConstants.VALIDATE_TOKEN_API_DESCRIPTION)
    @PostMapping("/validate_token")
    @ResponseStatus(HttpStatus.OK)
    ValidationResponse validateToken(@RequestAttribute(value = "X-USER-ID", required = false) String userId,
                                     @RequestAttribute(value = "X-AUTHORITIES", required = false) List<GrantedAuthority> authorities) {
        return new ValidationResponse(userId, authorities);
    }

}
