package com.oxygensend.auth.context;

import com.oxygensend.auth.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public User getAuthenticationPrinciple() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
