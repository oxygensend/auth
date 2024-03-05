package com.oxygensend.auth.infrastructure.mongo;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile("mongo")
@Builder
@Document("users")
record UserMongo(@Id UUID id,
                 String email,
                 String username,
                 String password,
                 Boolean locked,
                 Set<String> roles,
                 boolean verified) {

}
