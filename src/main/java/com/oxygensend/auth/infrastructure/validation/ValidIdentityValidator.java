package com.oxygensend.auth.infrastructure.validation;

import com.oxygensend.auth.context.IdentityProvider;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;

public class ValidIdentityValidator extends AbstractEmailValidator<ValidIdentity> {
    private final IdentityProvider identityProvider;

    public ValidIdentityValidator(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        var identityType = identityProvider.getIdentityType();
        return switch (identityType) {
            case EMAIL -> isEmailValid(value.toString(), context);
            case USERNAME -> isUsernameValid(value.toString(), context);
        };
    }


    private boolean isEmailValid(String value, ConstraintValidatorContext context) {
        return value == null || super.isValid(value, context) && !value.isEmpty();
    }

    private boolean isUsernameValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.length() >= 4 && value.length() <= 64) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("invalid username")
               .addConstraintViolation();
        return false;
    }


}

