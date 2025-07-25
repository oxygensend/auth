package com.oxygensend.auth.application.business_callback;

import com.oxygensend.auth.domain.model.identity.EmailAddress;
import com.oxygensend.auth.domain.model.identity.UserId;

public record BusinessRequestDto(UserId userId,
                                 String firstName,
                                 String lastName,
                                 EmailAddress email) {
}
