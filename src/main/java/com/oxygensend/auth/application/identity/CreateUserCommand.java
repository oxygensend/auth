package com.oxygensend.auth.application.identity;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;

import java.util.Set;

public record CreateUserCommand(UserId userId,
                                EmailAddress email,
                                Username username,
                                String rawPassword,
                                Set<Role> roles,
                                boolean verified,
                                BusinessId businessId
) {
}
