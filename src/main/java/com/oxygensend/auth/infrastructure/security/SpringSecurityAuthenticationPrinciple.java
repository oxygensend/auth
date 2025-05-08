package com.oxygensend.auth.infrastructure.security;

import com.oxygensend.auth.application.auth.security.AuthenticationPrinciple;
import com.oxygensend.auth.domain.model.identity.User;
import org.springframework.security.core.context.SecurityContextHolder;


final class SpringSecurityAuthenticationPrinciple implements AuthenticationPrinciple {
    @Override
    public User get() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
