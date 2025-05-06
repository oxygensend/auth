package com.oxygensend.auth.port.adapter.in.rest.resources.user;


import com.oxygensend.auth.application.auth.security.Admin;
import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.PasswordChangeRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.UserIdRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.PasswordResetRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.auth.request.VerifyEmailRequest;
import com.oxygensend.auth.port.adapter.in.rest.resources.SwaggerConstants;
import com.oxygensend.commons_jdk.DefaultView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Profile(Ports.REST)
@Tag(name = SwaggerConstants.USER_NAME, description = SwaggerConstants.USER_DESCRIPTION)
@CrossOrigin
@RestController
@RequestMapping("/v1/users")
class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @Admin
    @Operation(summary = SwaggerConstants.USER_DELETE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        service.delete(new UserId(id));
    }

    @Admin
    @Operation(summary = SwaggerConstants.USER_BLOCK_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/block")
    void block(@PathVariable UUID id) {
        service.block(new UserId(id));
    }


    @Admin
    @Operation(summary = SwaggerConstants.USER_UNBLOCK_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/unblock")
    void unblock(@PathVariable UUID id) {
        service.unblock(new UserId(id));
    }

    @Operation(summary = SwaggerConstants.USER_VERIFY_EMAIL_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify_email")
    DefaultView verifyEmail(@Validated @RequestBody VerifyEmailRequest request) {
        service.verifyEmail(request.token());
        return DefaultView.of("User login successfully verified");
    }

    @Operation(summary = SwaggerConstants.USER_GENERATE_EMAIL_VERIFICATION_TOKEN_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/generate_email_verification_token")
    TokenResponse generateEmailVerificationToken(@Validated @RequestBody UserIdRequest request) {
        var token = service.generateEmailVerificationToken(new UserId(request.id()));
        return new TokenResponse(token);
    }

    @Operation(summary = SwaggerConstants.USER_GENERATE_PASSWORD_RESET_TOKEN_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/generate_password_reset_token")
    TokenResponse generatePasswordResetToken(@Validated @RequestBody UserIdRequest request) {
        var token = service.generatePasswordResetToken(new UserId(request.id()));
        return new TokenResponse(token);
    }

    @Operation(summary = SwaggerConstants.USER_RESET_PASSWORD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reset_password")
    DefaultView resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        service.resetPassword(request.token(), request.newPassword());
        return DefaultView.of("Password successfully reset");
    }

    @Operation(summary = SwaggerConstants.USER_CHANGE_PASSWORD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change_password")
    DefaultView changePassword(@Validated @RequestBody PasswordChangeRequest request) {
        service.changePassword(request.oldPassword(), request.newPassword());
        return DefaultView.of("Password successfully changed");
    }


    @Admin
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void createUser(@Validated @RequestBody CreateUserRequest request) {
        service.createUser(request.toCommand());
    }
}
