package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.DataSourceObjectAdapter;
import com.oxygensend.auth.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("mongo")
@Component
final class UserMongoAdapter implements DataSourceObjectAdapter<User, UserMongo> {
    @Override
    public User toDomain(UserMongo userMongo) {
        return User.builder()
                   .id(userMongo.id())
                   .username(userMongo.username())
                   .email(userMongo.email())
                   .password(userMongo.password())
                   .locked(userMongo.locked())
                   .roles(userMongo.roles())
                   .verified(userMongo.verified())
                   .build();
    }

    @Override
    public UserMongo toDataSource(User user) {
        return UserMongo.builder()
                        .id(user.id())
                        .username(user.username())
                        .email(user.email())
                        .password(user.password())
                        .locked(user.locked())
                        .roles(user.roles())
                        .verified(user.verified())
                        .build();
    }

}
