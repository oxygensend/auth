package com.oxygensend.auth.domain.model.token.payload;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.token.AccessTokenSubject;
import com.oxygensend.auth.domain.model.token.TokenSubject;
import com.oxygensend.auth.domain.model.token.TokenType;
import com.oxygensend.auth.domain.model.identity.BusinessId;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.UserId;
import com.oxygensend.auth.domain.model.identity.UserName;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
final class AccessTokenPayloadFactory implements TokenPayloadFactory {


    @Override
    public TokenPayload createPayload(Date exp, Date iat, TokenSubject subject) {
        if (!(subject instanceof AccessTokenSubject)) {
            throw new IllegalArgumentException("Invalid token subject type: " + subject.getClass());
        }
        return new AccessTokenPayload(
            ((AccessTokenSubject) subject).userName(),
                subject.userId(),
                ((AccessTokenSubject) subject).roles(),
                iat,
                exp,
                ((AccessTokenSubject) subject).verified(),
                ((AccessTokenSubject) subject).businessId(),
                ((AccessTokenSubject) subject).email()
        );
    }

    @Override
    public TokenPayload createPayload(Claims claims) {
        return new AccessTokenPayload(
            new UserName(claims.get("username", String.class)),
            new UserId(claims.get("userId", String.class)),
            new HashSet<>(((List<Role>) claims.get("roles"))),
            claims.getIssuedAt(),
            claims.getExpiration(),
            claims.get("verified", Boolean.class),
            new BusinessId(claims.get("businessId", String.class)),
            new EmailAddress(claims.getSubject())
        );
    }

    @Override
    public TokenType getType() {
        return TokenType.ACCESS;
    }
}
