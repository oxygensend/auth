package com.oxygensend.auth.context.auth;


import com.oxygensend.auth.context.auth.request.AuthenticationRequest;
import com.oxygensend.auth.context.auth.request.RefreshTokenRequest;
import com.oxygensend.auth.context.auth.request.RegisterRequest;
import com.oxygensend.auth.context.auth.response.AuthenticationResponse;
import com.oxygensend.auth.context.auth.response.ValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

import static com.oxygensend.auth.config.SwaggerConstants.AUTHENTICATE_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.AUTH_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.AUTH_NAME;
import static com.oxygensend.auth.config.SwaggerConstants.REFRESH_TOKEN_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.REGISTER_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.VALIDATE_TOKEN_API_DESCRIPTION;


@Tag(name =AUTH_NAME, description = AUTH_DESCRIPTION)
@CrossOrigin
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = REGISTER_API_DESCRIPTION)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@RequestBody @Validated RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary =AUTHENTICATE_API_DESCRIPTION)
    @PostMapping("/access_token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse authenticate(@RequestBody @Validated AuthenticationRequest request) {
        return authService.authenticate(request);
    }

    @Operation(summary = REFRESH_TOKEN_API_DESCRIPTION)
    @PostMapping("/refresh_token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @Operation(summary = VALIDATE_TOKEN_API_DESCRIPTION)
    @PostMapping("/validate_token")
    @ResponseStatus(HttpStatus.OK)
    public ValidationResponse validateToken(@RequestAttribute(value = "X-USER-ID", required = false) UUID userId,
                                            @RequestAttribute(value = "X-AUTHORITIES", required = false) List<GrantedAuthority> authorities) {
        return authService.validateToken(userId, authorities);
    }

}
