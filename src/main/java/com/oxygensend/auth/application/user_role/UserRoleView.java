package com.oxygensend.auth.application.user_role;

record UserRoleView(String description) {
    private final static String ROLE_ADDED = "Role added successfully";
    private final static String ROLE_REMOVED = "Role removed successfully";

    static UserRoleView roleAdded() {
        return new UserRoleView(ROLE_ADDED);
    }

    static UserRoleView roleRemoved() {
        return new UserRoleView(ROLE_REMOVED);
    }
}
