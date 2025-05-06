package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

import com.oxygensend.auth.application.identity.UserRoleFeatureEnabled;
import com.oxygensend.auth.application.identity.UserRoleService;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.in.rest.resources.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import common.domain.model.DomainException;

@Profile(Ports.REST)
@Tag(name = SwaggerConstants.USER_ROLE_NAME, description = SwaggerConstants.USER_ROLE_DESCRIPTION)
@UserRoleFeatureEnabled
@CrossOrigin
@RestController
@RequestMapping("/v1/user-roles")
class UserRoleController {

    private final UserRoleService service;

    UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @Operation(summary = SwaggerConstants.USER_ROLE_ADD_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    UserRoleResponse addRoleToUser(@Validated @RequestBody UserRoleRequest request) {
        try {
            service.addRoleToUser(new UserId(request.userId()), new Role(request.role()));
            return UserRoleResponse.roleAdded();
        } catch (DomainException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @Operation(summary = SwaggerConstants.USER_ROLE_REMOVE_API_DESCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    UserRoleResponse removeRoleFromUser(@Validated @RequestBody UserRoleRequest request) {
        try {
            service.removeRoleFromUser(new UserId(request.userId()), new Role(request.role()));
            return UserRoleResponse.roleRemoved();
        } catch (DomainException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}
