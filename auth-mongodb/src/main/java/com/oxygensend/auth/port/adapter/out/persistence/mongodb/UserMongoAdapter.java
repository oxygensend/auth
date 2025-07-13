package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

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
final class UserMongoAdapter implements DataSourceObjectAdapter<User, UserMongo> {
    @Override
    public User toDomain(UserMongo userMongo) {
        return new User(new UserId(userMongo.id()),
                        new Credentials(new EmailAddress(userMongo.email()),
                                        new Username(userMongo.username()),
                                        userMongo.password() != null ? Password.fromHashed(userMongo.password()) :
                                        null),
                        userMongo.roles().stream().map(Role::new).collect(Collectors.toSet()),
                        userMongo.locked(),
                        userMongo.verified(),
                        new BusinessId(userMongo.businessId()),
                        userMongo.accountActivationType(),
                        userMongo.googleId() != null ? new GoogleId(userMongo.googleId()) : null);

    }

    @Override
    public UserMongo toDataSource(User user) {
        return new UserMongo(user.id().value(),
                             user.credentials().email().address(),
                             user.credentials().username().value(),
                             user.credentials().password() != null ? user.credentials().password().hashedValue() : null,
                             user.roles().stream().map(Role::value).collect(Collectors.toSet()),
                             user.isBlocked(),
                             user.isVerified(),
                             user.businessId().value(),
                             user.accountActivationType(),
                             user.googleId() != null ? user.googleId().value() : null);
    }

}
