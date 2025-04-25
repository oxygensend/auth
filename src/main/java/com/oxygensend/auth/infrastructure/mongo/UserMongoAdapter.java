package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.DataSourceObjectAdapter;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Credentials;
import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.Password;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.User;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Profile("mongo")
@Component
final class UserMongoAdapter implements DataSourceObjectAdapter<User, UserMongo> {
    @Override
    public User toDomain(UserMongo userMongo) {
        return new User(new UserId(userMongo.id()),
                        new Credentials(new EmailAddress(userMongo.email()),
                        new UserName(userMongo.username()),
                                        Password.fromHashed(userMongo.password())),
                        userMongo.roles().stream().map(Role::new).collect(Collectors.toSet()),
                        userMongo.locked(),
                        userMongo.verified(),
                        new BusinessId(userMongo.businessId()));

    }

    @Override
    public UserMongo toDataSource(User user) {
        return new UserMongo(user.id().value(),
                             user.credentials().email().address(),
                             user.credentials().userName().value(),
                             user.credentials().password().hashedValue(),
                             user.roles().stream().map(Role::value).collect(Collectors.toSet()),
                             user.isBlocked(),
                             user.isVerified(),
                             user.businessId().value());
    }

}
