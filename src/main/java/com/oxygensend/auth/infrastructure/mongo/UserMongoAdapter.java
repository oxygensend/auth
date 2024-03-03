package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.DataSourceObjectAdapter;
import com.oxygensend.auth.domain.User;
import org.springframework.stereotype.Component;

@Component
final class UserMongoAdapter implements DataSourceObjectAdapter<User, UserMongo> {
    @Override
    public User toDomain(UserMongo userMongo) {
        return User.builder()
                   .id(userMongo.id())
                   .firstName(userMongo.firstName())
                   .lastName(userMongo.lastName())
                   .username(userMongo.username())
                   .email(userMongo.email())
                   .password(userMongo.password())
                   .enabled(userMongo.enabled())
                   .locked(userMongo.locked())
                   .roles(userMongo.roles())
                   .emailValidated(userMongo.emailValidated())
                   .createdAt(userMongo.createdAt())
                   .build();
    }

    @Override
    public UserMongo toDataSource(User user) {
        return UserMongo.builder()
                        .id(user.id())
                        .firstName(user.firstName())
                        .lastName(user.lastName())
                        .username(user.username())
                        .email(user.email())
                        .password(user.password())
                        .enabled(user.enabled())
                        .locked(user.locked())
                        .roles(user.roles())
                        .emailValidated(user.emailValidated())
                        .createdAt(user.createdAt())
                        .build();
    }

}
