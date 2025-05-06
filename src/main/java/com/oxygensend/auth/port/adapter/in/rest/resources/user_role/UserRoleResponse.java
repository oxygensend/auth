package com.oxygensend.auth.port.adapter.in.rest.resources.user_role;

record UserRoleResponse(String description) {
    private final static String ROLE_ADDED = "Role added successfully";
    private final static String ROLE_REMOVED = "Role removed successfully";

    static UserRoleResponse roleAdded() {
        return new UserRoleResponse(ROLE_ADDED);
    }

    static UserRoleResponse roleRemoved() {
        return new UserRoleResponse(ROLE_REMOVED);
    }
}
