package com.oxygensend.auth.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
public record User(UUID id,
                   String email,
                   String username,
                   String password,
                   boolean locked,
                   Set<String> roles,
                   boolean verified,
                   String businessId) implements UserDetails {

    public User {
        if (roles == null) {
            roles = new HashSet<>();
        }
    }

    public User withNewRole(String role) {
        roles.add(role);
        return this;
    }

    public User withoutRole(String role) {
        roles.remove(role);
        return this;
    }

    public User blocked() {
        return new User(id, email, username, password, true, roles, verified, businessId);
    }

    public User unblocked() {
        return new User(id, email, username, password, false, roles, verified, businessId);
    }

    public User withNewPassword(String newPassword) {
        return new User(id, email, username, newPassword, locked, roles, verified, businessId);
    }

    public User withPasswordReset(String newPassword) {
        return new User(id, email, username, newPassword, locked, roles, true, businessId);
    }

    public User withEmailVerified() {
        return new User(id, email, username, password, locked, roles, true, businessId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
