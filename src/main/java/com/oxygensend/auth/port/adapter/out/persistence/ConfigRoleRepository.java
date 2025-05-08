package com.oxygensend.auth.port.adapter.out.persistence;

import com.oxygensend.auth.domain.model.identity.Role;
import com.oxygensend.auth.domain.model.identity.RoleRepository;
import com.oxygensend.auth.infrastructure.app_config.properties.SettingsProperties;
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
        return settingsProperties.allRoles()
                                 .stream()
                                 .map(Role::new)
                                 .toList();
    }

    @Override
    public boolean isAdminRole(Role role) {
        return settingsProperties.adminRoles()
                                 .contains(role.value());
    }

    @Override
    public boolean exists(Role role) {
        return settingsProperties.allRoles()
                                 .contains(role.value());
    }
}
