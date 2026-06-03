package org.karar.dev.common.security;

import org.karar.dev.common.enums.Role;

public final class SecurityRoles {

    private SecurityRoles() {

    }

    public static final String ADMIN = Role.ADMIN.name();
    public static final String USER = Role.USER.name();
    public static final String COMPANY = Role.COMPANY.name();

    public static final String[] ALL_USERS = { USER, COMPANY, ADMIN };
}

