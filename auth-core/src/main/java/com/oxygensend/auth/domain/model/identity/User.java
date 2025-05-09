package com.oxygensend.auth.domain.model.identity;

import com.oxygensend.auth.domain.model.identity.event.AddedRoleEvent;
import com.oxygensend.auth.domain.model.identity.event.BlockedEvent;
import com.oxygensend.auth.domain.model.identity.event.PasswordChangedEvent;
import com.oxygensend.auth.domain.model.identity.event.PasswordResetedEvent;
import com.oxygensend.auth.domain.model.identity.event.RegisteredEvent;
import com.oxygensend.auth.domain.model.identity.event.RemovedRoleEvent;
import com.oxygensend.auth.domain.model.identity.event.UnblockedEvent;
import com.oxygensend.auth.domain.model.identity.event.VerifiedEvent;
import com.oxygensend.auth.domain.model.identity.exception.PasswordMismatchException;
import com.oxygensend.auth.domain.model.identity.exception.RoleAlreadyAssignedException;
import com.oxygensend.auth.domain.model.identity.exception.RoleNotAssignedException;
import com.oxygensend.auth.domain.model.identity.exception.UserAlreadyExistsException;

import java.util.Objects;
import java.util.Set;

import common.AssertionConcern;
import common.domain.model.DomainAggregate;

public class User extends DomainAggregate {

    private final UserId id;
    private final Set<Role> roles;
    private final BusinessId businessId;
    private final AccountActivationType accountActivationType;
    private Credentials credentials;
    private boolean blocked;
    private boolean verified;

    // Used only for adapter needs, users should be created using the factory method
    public User(UserId id,
                Credentials credentials,
                Set<Role> roles,
                boolean locked,
                boolean verified,
                BusinessId businessId,
                AccountActivationType accountActivationType) {

        AssertionConcern.assertArgumentNotNull(id, "UserId cannot be null");
        AssertionConcern.assertArgumentNotNull(credentials, "Credentials cannot be null");
        AssertionConcern.assertArgumentNotNull(businessId, "BusinessId cannot be null");
        AssertionConcern.assertArgumentNotEmpty(roles, "Roles cannot be empty");
        AssertionConcern.assertArgumentNotNull(accountActivationType, "AccountActivationType cannot be null");

        this.id = id;
        this.credentials = credentials;
        this.blocked = locked;
        this.verified = verified;
        this.businessId = businessId;
        this.roles = roles;
        this.accountActivationType = accountActivationType;
    }

    public static User registerUser(UserId id,
                                    Credentials credentials,
                                    Set<Role> roles,
                                    BusinessId businessId,
                                    AccountActivationType accountActivation,
                                    UserUniquenessChecker userUniquenessChecker) {
        boolean isVerified = accountActivation == AccountActivationType.NONE;
        var newUser = new User(id, credentials, roles, false, isVerified, businessId, accountActivation);
        if (!userUniquenessChecker.isUnique(newUser)) {
            throw new UserAlreadyExistsException();
        }
        newUser.addEvent(new RegisteredEvent(newUser));
        return newUser;
    }

    public UserId id() {
        return id;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Username username() {
        return credentials.username();
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

    public AccountActivationType accountActivationType() {
        return accountActivationType;
    }

    public void addRole(Role newRole) {
        roles.stream()
             .filter(role -> Objects.equals(role, newRole))
             .findAny()
             .ifPresentOrElse(
                 role -> {
                     throw new RoleAlreadyAssignedException();
                 },
                 () -> {
                     roles.add(newRole);
                     events.add(new AddedRoleEvent(id, newRole));
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
                     events.add(new RemovedRoleEvent(id, roleToRemove));
                 },
                 () -> {
                     throw new RoleNotAssignedException();
                 }
             );
    }

    public void block() {
        AssertionConcern.assertStateFalse(isBlocked(), "Account is already blocked");
        blocked = true;

        events.add(new BlockedEvent(id));
    }

    public void unblock() {
        AssertionConcern.assertStateTrue(isBlocked(), "Account is already unblocked");
        blocked = false;
        events.add(new UnblockedEvent(id));
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

        events.add(new PasswordChangedEvent(id, email()));
    }

    public void resetPassword(String newPassword, PasswordService passwordService) {
        AssertionConcern.assertArgumentNotEmpty(newPassword, "New password cannot be null or empty");
        setNewEncryptedPassword(newPassword, passwordService);
        events.add(new PasswordResetedEvent(id));

        if (!verified && accountActivationType == AccountActivationType.CHANGE_PASSWORD) {
            this.verified = true;
            events.add(new VerifiedEvent(id));
        }
    }

    public void verifyEmail() {
        this.verified = true;
        events.add(new VerifiedEvent(id));
    }

    private void setNewEncryptedPassword(String password, PasswordService passwordService) {
        var encryptedPassword = Password.fromPlaintext(password, passwordService);
        this.credentials = credentials.passwordChanged(encryptedPassword);
    }


}
