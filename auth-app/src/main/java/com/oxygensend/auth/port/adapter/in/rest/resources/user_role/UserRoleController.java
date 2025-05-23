package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import com.oxygensend.auth.application.auth.security.Admin;
import com.oxygensend.auth.application.identity.UserRoleService;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.port.adapter.in.rest.resources.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = SwaggerConstants.USER_ROLE_NAME, description = SwaggerConstants.USER_ROLE_DESCRIPTION)
@CrossOrigin
@RestController
@RequestMapping("/v1/user-roles")
class UserRoleController {

    private final UserRoleService service;

    UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @Admin
    @Operation(summary = SwaggerConstants.USER_ROLE_ADD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    UserRoleResponse addRoleToUser(@Validated @RequestBody UserRoleRequest request) {
        service.addRoleToUser(new UserId(request.userId()), new Role(request.role()));
        return UserRoleResponse.roleAdded();

    }

    @Admin
    @Operation(summary = SwaggerConstants.USER_ROLE_REMOVE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    UserRoleResponse removeRoleFromUser(@Validated @RequestBody UserRoleRequest request) {
        service.removeRoleFromUser(new UserId(request.userId()), new Role(request.role()));
        return UserRoleResponse.roleRemoved();

    }
}
