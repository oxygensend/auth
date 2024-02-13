package com.oxygensend.auth.context.request;

import com.oxygensend.auth.domain.User;
import com.oxygensend.commons_jdk.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@Size(min = 2, max = 64) @NotBlank String firstName,
                              @Size(min = 2, max = 64) @NotBlank String lastName,
                              @NotNull @ValidEmail String email,
                              @Size(min = 4, max = 64) @NotBlank String password
) {

    // TODO: remove
    public User toUserEntity() {
        return User.builder()
                   .email(email)
                   .firstName(firstName)
                   .lastName(lastName)
                   .build();
    }
}
