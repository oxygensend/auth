package com.oxygensend.auth.port.adapter.out.persistence.mongodb;

import com.oxygensend.auth.domain.model.identity.AccountActivationType;
import com.oxygensend.auth.port.Ports;

import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Profile(Ports.MONGODB)
@Document("users")
@CompoundIndex(name = "email", def = "{'email': 1}")
record UserMongo(
        @Id UUID id,
        String email,
        String username,
        String password,
        Set<String> roles,
        Boolean locked,
        boolean verified,
        String businessId,
        AccountActivationType accountActivationType
) {

}
