package com.oxygensend.auth.context.user;


import com.oxygensend.auth.context.auth.request.PasswordChangeRequest;
import com.oxygensend.auth.context.auth.request.PasswordResetRequest;
import com.oxygensend.auth.context.auth.request.UserIdRequest;
import com.oxygensend.auth.context.auth.request.VerifyEmailRequest;
import com.oxygensend.auth.infrastructure.security.Admin;
import com.oxygensend.commons_jdk.DefaultView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

import static com.oxygensend.auth.config.SwaggerConstants.USER_BLOCK_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_CHANGE_PASSWORD_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_DELETE_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_GENERATE_EMAIL_VERIFICATION_TOKEN_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_GENERATE_PASSWORD_RESET_TOKEN_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_NAME;
import static com.oxygensend.auth.config.SwaggerConstants.USER_RESET_PASSWORD_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_UNBLOCK_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_VERIFY_EMAIL_API_DESCRIPTION;

@Tag(name = USER_NAME, description = USER_DESCRIPTION)
@CrossOrigin
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService service;

    @Admin
    @Operation(summary = USER_DELETE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @Admin
    @Operation(summary = USER_BLOCK_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/block")
    void block(@PathVariable UUID id) {
        service.block(id);
    }


    @Admin
    @Operation(summary = USER_UNBLOCK_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{id}/unblock")
    void unblock(@PathVariable UUID id) {
        service.unblock(id);
    }

    @Operation(summary = USER_VERIFY_EMAIL_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify_email")
    DefaultView verifyEmail(@Validated @RequestBody VerifyEmailRequest request) {
        service.verifyEmail(request);
        return DefaultView.of("User email successfully verified");
    }

    @Operation(summary = USER_GENERATE_EMAIL_VERIFICATION_TOKEN_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/generate_email_verification_token")
    DefaultView generateEmailVerificationToken(@Validated @RequestBody UserIdRequest request) {
        service.generateEmailVerificationToken(request.id());
        return DefaultView.of("Email verification token successfully generated");
    }

    @Operation(summary = USER_GENERATE_PASSWORD_RESET_TOKEN_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/generate_password_reset_token")
    DefaultView generatePasswordResetToken(@Validated @RequestBody UserIdRequest request) {
        service.generatePasswordResetToken(request.id());
        return DefaultView.of("Password reset token successfully generated");
    }

    @Operation(summary = USER_RESET_PASSWORD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reset_password")
    DefaultView resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        service.resetPassword(request);
        return DefaultView.of("Password successfully reset");
    }

    @Operation(summary = USER_CHANGE_PASSWORD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/change_password")
    DefaultView changePassword(@Validated @RequestBody PasswordChangeRequest request) {
        service.changePassword(request);
        return DefaultView.of("Password successfully changed");
    }

}
