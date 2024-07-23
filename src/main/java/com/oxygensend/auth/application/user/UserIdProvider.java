package com.oxygensend.auth.application.user;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserIdProvider {
    public UUID get() {
        return UUID.randomUUID();
    }
}
