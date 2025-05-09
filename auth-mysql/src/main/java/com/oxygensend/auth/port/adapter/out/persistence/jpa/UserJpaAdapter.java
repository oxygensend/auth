package com.oxygensend.auth.port.adapter.out.persistence.jpa;

import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.Username;
import com.oxygensend.auth.port.Ports;
import com.oxygensend.auth.port.adapter.out.persistence.DataSourceObjectAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
final class UserJpaAdapter implements DataSourceObjectAdapter<User, UserJpa> {
    @Override
    public User toDomain(UserJpa userJpa) {
        return new User(new UserId(userJpa.id),
                        new Credentials(new EmailAddress(userJpa.email),
                                        new Username(userJpa.username),
                                        Password.fromHashed(userJpa.password)),
                        userJpa.roles.stream().map(Role::new).collect(Collectors.toSet()),
                        userJpa.locked,
                        userJpa.verified,
                        new BusinessId(userJpa.businessId),
                        userJpa.accountActivationType);
    }

    @Override
    public UserJpa toDataSource(User user) {
        var userJpa = new UserJpa();
        userJpa.id = user.id().value();
        userJpa.businessId = user.businessId().value();
        userJpa.email = user.credentials().email().address();
        userJpa.username = user.credentials().username().value();
        userJpa.roles = user.roles().stream().map(Role::value).collect(Collectors.toSet());
        userJpa.password = user.credentials().password().hashedValue();
        userJpa.locked = user.isBlocked();
        userJpa.verified = user.isVerified();
        userJpa.accountActivationType = user.accountActivationType();
        return userJpa;
    }

}
