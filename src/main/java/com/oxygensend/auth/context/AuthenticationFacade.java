package com.oxygensend.auth.context;


import com.oxygensend.auth.domain.User;

public interface AuthenticationFacade {

    User getAuthenticationPrinciple();
}
