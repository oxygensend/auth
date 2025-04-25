package com.oxygensend.auth.ui.user_role;

import com.oxygensend.auth.application.identity.UserRoleService;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.application.identity.UserRoleEndpointEnabled;
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

import static com.oxygensend.auth.ui.SwaggerConstants.USER_ROLE_ADD_API_DESCRIPTION;
import static com.oxygensend.auth.ui.SwaggerConstants.USER_ROLE_DESCRIPTION;
import static com.oxygensend.auth.ui.SwaggerConstants.USER_ROLE_NAME;
import static com.oxygensend.auth.ui.SwaggerConstants.USER_ROLE_REMOVE_API_DESCRIPTION;

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
    UserRoleResponse addRoleToUser(@Validated @RequestBody UserRoleRequest request) {
        service.addRoleToUser(new UserId(request.userId()), new Role(request.role()));
        return UserRoleResponse.roleAdded();
    }

    @Operation(summary = USER_ROLE_REMOVE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    UserRoleResponse removeRoleFromUser(@Validated @RequestBody UserRoleRequest request) {
        service.removeRoleFromUser(new UserId(request.userId()), new Role(request.role()));
        return UserRoleResponse.roleRemoved();
    }
}
