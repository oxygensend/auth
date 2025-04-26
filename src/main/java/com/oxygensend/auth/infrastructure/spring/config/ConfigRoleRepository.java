package com.oxygensend.auth.infrastructure.spring.config;

import com.oxygensend.auth.infrastructure.spring.config.properties.SettingsProperties;
import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
final class ConfigRoleRepository implements RoleRepository {

    private final SettingsProperties settingsProperties;

    public ConfigRoleRepository(SettingsProperties settingsProperties) {
        this.settingsProperties = settingsProperties;
    }

    @Override
    public List<Role> findAll() {
        return settingsProperties.roles()
                .stream()
                .map(Role::new)
                .toList();
    }

    @Override
    public boolean exists(Role role) {
        return settingsProperties.roles()
                .contains(role.value());
    }
}
