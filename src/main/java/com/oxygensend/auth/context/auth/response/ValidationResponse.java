package com.oxygensend.auth.context.auth.response;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public record ValidationResponse(
        String userId,
        List<GrantedAuthority> authorities
) {

}
