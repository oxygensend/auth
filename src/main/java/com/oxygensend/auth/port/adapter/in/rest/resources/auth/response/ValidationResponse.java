package com.oxygensend.auth.port.adapter.in.rest.resources.auth.response;

import com.oxygensend.auth.port.Ports;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;

@Profile(Ports.REST)
public record ValidationResponse(
        String userId,
        List<GrantedAuthority> authorities
) {

}
