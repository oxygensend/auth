package com.oxygensend.auth.domain.model.identity;

import java.util.Set;
import java.util.UUID;

public class UserMother {
    
    public static UserId userId() {
        return new UserId(UUID.randomUUID());
    }
    
    public static UserId userId(String value) {
        return new UserId(value);
    }
    
    public static BusinessId businessId() {
        return new BusinessId("B" + UUID.randomUUID().toString().substring(0, 8));
    }
    
    public static BusinessId businessId(String value) {
        return new BusinessId(value);
    }
    
    public static EmailAddress email() {
        return new EmailAddress("test@example.com");
    }
    
    public static EmailAddress email(String address) {
        return new EmailAddress(address);
    }
    
    public static Username username() {
        return new Username("testuser");
    }
    
    public static Username username(String value) {
        return new Username(value);
    }
    
    public static Password password(PasswordService passwordService) {
        return Password.fromPlaintext("password123", passwordService);
    }
    
    public static Password hashedPassword(String hashedValue) {
        return Password.fromHashed(hashedValue);
    }
    
    public static Role role() {
        return new Role("USER");
    }
    
    public static Role role(String value) {
        return new Role(value);
    }
    
    public static Set<Role> roles() {
        return Set.of(role());
    }
    
    public static Set<Role> roles(String... values) {
        return Set.of(values).stream().map(Role::new).collect(java.util.stream.Collectors.toSet());
    }
    
    public static Credentials credentials(PasswordService passwordService) {
        return new Credentials(email(), username(), password(passwordService));
    }
    
    public static Credentials credentials(EmailAddress email, Username username, Password password) {
        return new Credentials(email, username, password);
    }
    
    public static Credentials expiredCredentials(PasswordService passwordService) {
        return new Credentials(email(), username(), password(passwordService), true);
    }
}
