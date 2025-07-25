package com.oxygensend.auth.application;

import com.oxygensend.auth.domain.model.identity.Role;

import java.util.Set;

public record GoogleOAuthData(String clientId,
                              String clientSecret,
                              String redirectUri,
                              String businessCallbackUrl,
                              String authUrl,
                              Set<Role> defaultRoles
                              ) {
}
