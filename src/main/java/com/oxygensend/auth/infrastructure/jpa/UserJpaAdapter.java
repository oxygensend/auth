//package com.oxygensend.auth.infrastructure.jpa;
//
//import com.oxygensend.auth.domain.DataSourceObjectAdapter;
//import com.oxygensend.auth.domain.User;
//import org.springframework.stereotype.Component;
//
//@Component
//final class UserJpaAdapter implements DataSourceObjectAdapter<User, UserJpa> {
//    @Override
//    public User toDomain(UserJpa userMongo) {
//        return User.builder()
//                   .id(userMongo.id())
//                   .firstName(userMongo.name())
//                   .lastName(userMongo.surname())
//                   .username(userMongo.username())
//                   .email(userMongo.email())
//                   .password(userMongo.password())
//                   .enabled(userMongo.enabled())
//                   .locked(userMongo.locked())
//                   .roles(userMongo.roles())
//                   .emailValidated(userMongo.verified())
//                   .createdAt(userMongo.createdAt())
//                   .build();
//    }
//
//    @Override
//    public UserJpa toDataSource(User user) {
//        return UserJpa.builder()
//                      .id(user.id())
//                      .name(user.firstName())
//                      .surname(user.lastName())
//                      .username(user.username())
//                      .email(user.email())
//                      .password(user.password())
//                      .enabled(user.enabled())
//                      .locked(user.locked())
//                      .roles(user.roles())
//                      .verified(user.verified())
//                      .createdAt(user.createdAt())
//                      .build();
//    }
//
//}
