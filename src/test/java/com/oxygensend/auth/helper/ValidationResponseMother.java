package com.oxygensend.auth.helper;

import com.oxygensend.auth.ui.auth.response.ValidationResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;

public class ValidationResponseMother {

    public static ValidationResponse authorized() {
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
        return new ValidationResponse(
                UUID.randomUUID().toString(),
                grantedAuthorityList
        );
    }

    public static ValidationResponse unAuthorized() {
        return new ValidationResponse(
                null,
                null
        );
    }

}
