package com.oxygensend.auth.application.auth.security;


import com.oxygensend.auth.domain.model.identity.User;

public interface AuthenticationPrinciple {

    User get();
}
