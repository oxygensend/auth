package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.AccountActivation;
import com.oxygensend.auth.domain.model.identity.exception.PasswordMismatchException;
import com.oxygensend.auth.domain.model.identity.exception.RoleAlreadyExistsException;
import com.oxygensend.auth.domain.model.identity.exception.RoleNotAssignedException;
import lombok.Builder;

import java.util.Objects;
import java.util.Set;

import common.AssertionConcern;

@Builder(toBuilder = true)
public class User {

    private final UserId id;
    private Credentials credentials;
    private Set<Role> roles;
    private boolean blocked;
    private boolean verified;
    private BusinessId businessId;

    public User(UserId id,
                Credentials credentials,
                Set<Role> roles,
                boolean locked,
                boolean verified,
                BusinessId businessId) {

        AssertionConcern.assertArgumentNotNull(id, "UserId cannot be null");
        AssertionConcern.assertArgumentNotNull(credentials, "Credentials cannot be null");
        AssertionConcern.assertArgumentNotNull(businessId, "BusinessId cannot be null");

        this.id = id;
        this.credentials = credentials;
        this.blocked = locked;
        this.verified = verified;
        this.businessId = businessId;
        this.roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public static User registerNewUser(UserId id,
                                       Credentials credentials,
                                       Set<Role> roles,
                                       BusinessId businessId,
                                       AccountActivation accountActivation) {
        boolean isVerified = accountActivation == AccountActivation.NONE;
        return new User(id, credentials, roles, false, isVerified, businessId);
    }

    public UserId id() {
        return id;
    }
    public boolean isBlocked() {
        return blocked;
    }

    public UserName userName() {
        return credentials.userName();
    }

    public EmailAddress email() {
        return credentials.email();
    }

    public Password password() {
        return credentials.password();
    }
    public boolean isCredentialsExpired() {
        return !credentials.isNonExpired();
    }
    public Credentials credentials() {
        return credentials;
    }

    public Set<Role> roles() {
        return roles;
    }

    public boolean isVerified() {
        return verified;
    }

    public BusinessId businessId() {
        return businessId;
    }

    public void addRole(Role newRole) {
        roles.stream()
             .filter(role -> Objects.equals(role, newRole))
             .findAny()
             .ifPresentOrElse(
                 role -> {
                     throw new RoleAlreadyExistsException();
                 },
                 () -> {
                     roles.add(newRole);
                 }
             );
    }

    public void removeRole(Role roleToRemove) {
        roles.stream()
             .filter(role -> Objects.equals(role, roleToRemove))
             .findAny()
             .ifPresentOrElse(
                 role -> {
                     roles.remove(roleToRemove);
                 },
                 () -> {
                     throw new RoleNotAssignedException();
                 }
             );
    }

    public void block() {
        AssertionConcern.assertStateFalse(isBlocked(), "Account is already blocked");
        blocked = true;
    }

    public void unblock() {
        AssertionConcern.assertStateTrue(isBlocked(), "Account is already unblocked");
        blocked = false;
    }

    public boolean authenticateWithPassword(String password, PasswordService passwordService) {
        AssertionConcern.assertArgumentNotEmpty(password, "Password cannot be null or empty");
        return credentials.passwordMatches(password, passwordService);
    }

    public void changePassword(String oldPassword, String newPassword, PasswordService passwordService) {
        AssertionConcern.assertArgumentNotEmpty(newPassword, "New password cannot be null or empty");
        if (!credentials.passwordMatches(oldPassword, passwordService)) {
            throw new PasswordMismatchException("Old password does not match");
        }
        setNewEncryptedPassword(newPassword, passwordService);
    }

    public void resetPassword(String newPassword, PasswordService passwordService) {
        AssertionConcern.assertArgumentNotEmpty(newPassword, "New password cannot be null or empty");
        setNewEncryptedPassword(newPassword, passwordService);
        this.verified = true;
    }

    public void verifyEmail() {
        this.verified = true;
    }

    private void setNewEncryptedPassword(String password, PasswordService passwordService) {
        var encryptedPassword = Password.fromPlaintext(password, passwordService);
        this.credentials = credentials.passwordChanged(encryptedPassword);
    }


}
