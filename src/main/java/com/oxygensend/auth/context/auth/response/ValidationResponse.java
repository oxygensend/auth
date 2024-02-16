package com.oxygensend.auth.context.auth.response;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public record ValidationResponse(
        boolean isAuthorized,

        UUID userId,
        List<GrantedAuthority> authorities
){

}
