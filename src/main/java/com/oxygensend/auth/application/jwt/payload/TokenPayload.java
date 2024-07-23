package com.oxygensend.auth.application.jwt.payload;


import com.oxygensend.auth.domain.TokenType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode
@ToString
abstract public class TokenPayload implements ClaimsPayload {
    protected final TokenType type;
    protected final Date iat;
    protected final Date exp;
}
