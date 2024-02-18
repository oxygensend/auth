package com.oxygensend.auth.context;

import com.oxygensend.auth.domain.User;

public record SendMailCommand(User user, String subject, String message) {
}
