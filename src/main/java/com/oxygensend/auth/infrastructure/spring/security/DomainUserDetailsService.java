package com.oxygensend.auth.infrastructure.spring.security;

import com.oxygensend.auth.application.identity.UserService;
import com.oxygensend.auth.application.settings.LoginProvider;
import com.oxygensend.auth.domain.model.identity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;

public class DomainUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final LoginProvider loginProvider;

    public DomainUserDetailsService( UserService userService, LoginProvider loginProvider) {
        this.userService = userService;
        this.loginProvider = loginProvider;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.userByLogin(loginProvider.get(username)).orElse(null);

        return new org.springframework.security.core.userdetails.User(
            loginProvider.get(user.credentials()).value(),
            user.password().hashedValue(),
            true,
            true,
           !user.isCredentialsExpired(),
            user.isBlocked(),
            user.roles().stream().map(role -> new SimpleGrantedAuthority(role.value())).collect(Collectors.toSet()));
    }
}
