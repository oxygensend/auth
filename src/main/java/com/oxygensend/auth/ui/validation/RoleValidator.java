package com.oxygensend.auth.ui.validation;

import com.oxygensend.auth.infrastructure.spring.config.properties.SettingsProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    private final Set<String> definedRoles;

    public RoleValidator(SettingsProperties settingsProperties) {
        this.definedRoles = settingsProperties.roles();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || definedRoles.contains(s)) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("not acceptable role " + s)
                                  .addConstraintViolation();

        return false;
    }
}
