package com.oxygensend.auth.context.user_role;

import com.oxygensend.auth.infrastructure.settings.UserRoleEndpointEnabled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.oxygensend.auth.config.SwaggerConstants.USER_ROLE_ADD_API_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_ROLE_DESCRIPTION;
import static com.oxygensend.auth.config.SwaggerConstants.USER_ROLE_NAME;
import static com.oxygensend.auth.config.SwaggerConstants.USER_ROLE_REMOVE_API_DESCRIPTION;

@Tag(name = USER_ROLE_NAME, description = USER_ROLE_DESCRIPTION)
@UserRoleEndpointEnabled
@CrossOrigin
@RestController
@RequestMapping("/v1/user-roles")
@RequiredArgsConstructor
class UserRoleController {

    private final UserRoleService service;

    @Operation(summary = USER_ROLE_ADD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    UserRoleView addRoleToUser(@Validated @RequestBody UserRoleRequest request) {
        service.addRoleToUser(request);
        return UserRoleView.roleAdded();
    }

    @Operation(summary = USER_ROLE_REMOVE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    UserRoleView removeRoleFromUser(@Validated @RequestBody UserRoleRequest request) {
        service.removeRoleFromUser(request);
        return UserRoleView.roleRemoved();
    }
}
