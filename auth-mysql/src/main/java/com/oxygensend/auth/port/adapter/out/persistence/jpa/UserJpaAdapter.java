package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.GoogleId;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
final class UserJpaAdapter implements DataSourceObjectAdapter<User, UserJpa> {
    @Override
    public User toDomain(UserJpa userJpa) {
        return new User(new UserId(userJpa.id),
                        new Credentials(new EmailAddress(userJpa.email),
                                        new Username(userJpa.username),
                                        userJpa.password != null ? Password.fromHashed(userJpa.password) : null),
                        userJpa.roles.stream().map(Role::new).collect(Collectors.toSet()),
                        userJpa.locked,
                        userJpa.verified,
                        userJpa.businessId != null ? new BusinessId(userJpa.businessId) : null,
                        userJpa.accountActivationType,
                        userJpa.googleId != null ? new GoogleId(userJpa.googleId) : null);
    }

    @Override
    public UserJpa toDataSource(User user) {
        var userJpa = new UserJpa();
        userJpa.id = user.id().value();
        userJpa.businessId = user.businessId() != null ? user.businessId().value() : null;
        userJpa.email = user.credentials().email().address();
        userJpa.username = user.credentials().username().value();
        userJpa.roles = user.roles().stream().map(Role::value).collect(Collectors.toSet());
        userJpa.password = user.password() != null ? user.password().hashedValue() : null;
        userJpa.locked = user.isBlocked();
        userJpa.verified = user.isVerified();
        userJpa.accountActivationType = user.accountActivationType();
        userJpa.googleId = user.googleId() != null ? user.googleId().value() : null;
        return userJpa;
    }

}
