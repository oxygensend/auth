package com.oxygensend.auth.application.auth;


import com.oxygensend.auth.domain.User;

public interface AuthenticationFacade {

    User getAuthenticationPrinciple();
}
