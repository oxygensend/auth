package com.oxygensend.auth.infrastructure.domain;

import com.oxygensend.auth.domain.model.identity.PasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;

class BcryptPasswordService implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
