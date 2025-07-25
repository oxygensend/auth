package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document("users")
@CompoundIndex(name = "email", def = "{'email': 1}")
record UserMongo(
    @Id UUID id,
    String email,
    String username,
    String password,
    Set<String> roles,
    boolean locked,
    boolean verified,
    String businessId,
    AccountActivationType accountActivationType,
    String googleId
) {

}
