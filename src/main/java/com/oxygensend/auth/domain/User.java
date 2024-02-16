package com.oxygensend.auth.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Document("users")
public record User(@Id UUID id,
                   String firstName,
                   String lastName,
                   String email,
                   String password,
                   Boolean enabled,
                   Boolean locked,
                   Set<UserRole> roles,
                   LocalDateTime emailValidated,
                   LocalDateTime createdAt) implements UserDetails {

    public User withNewRole(UserRole role) {
        roles.add(role);
        return this;
    }

    public User withoutRole(UserRole role) {
        roles.remove(role);
        return this;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                    .map(UserRole::name)
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
        return enabled;
    }
}
