package com.oxygensend.auth.domain.model.identity;

import java.util.List;

public interface RoleRepository {
    List<Role> findAll();

    boolean exists(Role role);
}
