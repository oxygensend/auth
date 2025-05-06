package com.oxygensend.auth.port.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD,ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    String message() default "invalid email address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
