package org.karar.dev.common.security;

import org.karar.dev.common.enums.Role;

/**
 * Centralized role constants for security configuration.
 * Avoids scattering role name strings across multiple classes.
 */
public final class SecurityRoles {

    private SecurityRoles() {

    }

    public static final String ADMIN = Role.ADMIN.name();
    public static final String USER = Role.USER.name();
    public static final String COMPANY = Role.COMPANY.name();


    public static final String[] ALL_USERS = { USER, COMPANY, ADMIN };
}
