package com.oxygensend.auth.context.user_role;

import com.oxygensend.auth.infrastructure.security.Admin;
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
@RequestMapping("/v1/user-roles")
@RequiredArgsConstructor
class UserRoleController {

    private final UserRoleService service;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/add")
    UserRoleView addRoleToUser(@Validated @RequestBody UserRoleRequest request) {
        service.addRoleToUser(request);
        return UserRoleView.roleAdded();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/remove")
    UserRoleView removeRoleFromUser(@Validated @RequestBody UserRoleRequest request) {
        service.removeRoleFromUser(request);
        return UserRoleView.roleRemoved();
    }
}
