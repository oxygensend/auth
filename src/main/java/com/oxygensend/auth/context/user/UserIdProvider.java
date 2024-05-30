package com.oxygensend.auth.context.user;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserIdProvider {
    public UUID get() {
        return UUID.randomUUID();
    }
}
