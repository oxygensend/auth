package com.oxygensend.auth.application.auth.security;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AdminAccessChecker {

    private final RoleRepository roleRepository;

    public AdminAccessChecker(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority)
                          .anyMatch(role -> roleRepository.isAdminRole(new Role(role)));
    }
}
