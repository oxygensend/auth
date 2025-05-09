package com.oxygensend.auth.domain.model.identity;

public interface PasswordService {
    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
