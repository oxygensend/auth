package com.oxygensend.auth.infrastructure.mongo;

import com.oxygensend.auth.domain.UserRole;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document("users")
record UserMongo(@Id UUID id,
                 String firstName,
                 String lastName,
                 String email,
                 String password,
                 Boolean enabled,
                 Boolean locked,
                 Set<UserRole> roles,
                 LocalDateTime emailValidated,
                 LocalDateTime createdAt) {

}
