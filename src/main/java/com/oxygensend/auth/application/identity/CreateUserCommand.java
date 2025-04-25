package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.ui.user.CreateUserRequest;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;

import java.util.Set;
import java.util.stream.Collectors;

public record CreateUserCommand(UserId userId,
    EmailAddress email,
    UserName userName,
    String rawPassword,
    Set<Role> roles,
    boolean verified,
    BusinessId businessId
) {
    public static CreateUserCommand fromRequest(CreateUserRequest request) {

        return new CreateUserCommand(
            new UserId(request.id()),
            new EmailAddress(request.email()),
            new UserName(request.userName()),
            request.password(),
            request.roles().stream().map(Role::new).collect(Collectors.toSet()),
            request.verified(),
            new BusinessId(request.businessId())
        );
    }
}
