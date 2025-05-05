package com.oxygensend.auth.infrastructure.persistence;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile("mongo")
@Builder
@Document("users")
@CompoundIndex(name = "email", def = "{'email': 1}")
record UserMongo(@Id UUID id,
                 String email,
                 String username,
                 String password,
                 Set<String> roles,
                 Boolean locked,
                 boolean verified,
                 String businessId,
                 AccountActivationType accountActivationType) {

}
