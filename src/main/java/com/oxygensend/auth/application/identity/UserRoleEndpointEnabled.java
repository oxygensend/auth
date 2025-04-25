package com.oxygensend.auth.application.identity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Documented
@Target( {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix = "auth.settings.user-role-endpoint", name = "enabled", havingValue = "true")
public @interface UserRoleEndpointEnabled {
}
