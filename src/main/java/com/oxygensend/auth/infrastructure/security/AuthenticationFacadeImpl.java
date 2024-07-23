package com.oxygensend.auth.infrastructure.security;

import com.oxygensend.auth.application.auth.AuthenticationFacade;
import com.oxygensend.auth.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
final class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public User getAuthenticationPrinciple() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
