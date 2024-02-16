package com.oxygensend.auth.context.auth;


import com.oxygensend.auth.domain.User;

public interface AuthenticationFacade {

    User getAuthenticationPrinciple();
}
