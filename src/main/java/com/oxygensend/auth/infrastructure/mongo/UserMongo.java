package com.oxygensend.auth.infrastructure.mongo;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Indexed;

@Profile("mongo")
@Builder
@Document("users")
@CompoundIndex(name = "email", def = "{'email': 1}")
record UserMongo(@Id UUID id,
                 String email,
                 String username,
                 String password,
                 Boolean locked,
                 Set<String> roles,
                 boolean verified,
                 String businessId) {

}
