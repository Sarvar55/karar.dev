package org.karar.dev.common.security;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Centralized endpoint authorization rules.
 * <p>
 * All paths are declared explicitly with their HTTP method and access level.
 * Grouped into two clear sections: PUBLIC endpoints and SECURED endpoints.
 */
public final class SecurityPathConfig {

    private SecurityPathConfig() {

    }


    public static void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
        configurePublicPaths(requests);
        configureSecuredPaths(requests);
        requests.anyRequest().authenticated();
    }

    // ========================
    // PUBLIC ENDPOINTS
    // ========================

    private static void configurePublicPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {

        requests.requestMatchers("/api/v1/auth/**").permitAll();


        requests.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();


        requests.requestMatchers(PathRequest.toH2Console()).permitAll();

        // --- Decisions (read-only) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/decisions").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/decisions/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/decisions/{decisionId}/comments").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/decisions/{decisionId}/tags").permitAll();

        // --- Tags (read-only) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/tags").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/tags/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/tags/name/{name}").permitAll();

        // --- Comments (read-only) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/comments").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/comments/{id}").permitAll();

        // --- Votes (public queries) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/votes").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/votes/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/votes/check").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/votes/decisions/{decisionId}/count").permitAll();

        // --- Users (read-only) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/users").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/users/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/comments").permitAll();

        // --- Companies (read-only) ---
        requests.requestMatchers(HttpMethod.GET, "/api/v1/companies").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/v1/companies/{id}").permitAll();
    }

    // ========================
    // SECURED ENDPOINTS
    // ========================

    private static void configureSecuredPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {

        requests.requestMatchers(HttpMethod.POST, "/api/v1/decisions").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.PUT, "/api/v1/decisions/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/decisions/{id}").hasAnyRole(SecurityRoles.ALL_USERS);


        requests.requestMatchers(HttpMethod.POST, "/api/v1/tags").hasRole(SecurityRoles.ADMIN);
        requests.requestMatchers(HttpMethod.PUT, "/api/v1/tags/{id}").hasRole(SecurityRoles.ADMIN);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/tags/{id}").hasRole(SecurityRoles.ADMIN);


        requests.requestMatchers(HttpMethod.POST, "/api/v1/comments").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.PUT, "/api/v1/comments/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/comments/{id}").hasAnyRole(SecurityRoles.ALL_USERS);


        // --- Votes (authenticated voting) ---
        requests.requestMatchers(HttpMethod.POST, "/api/v1/votes").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/votes/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/votes/users/{userId}/decisions/{decisionId}").hasAnyRole(SecurityRoles.ALL_USERS);

        // --- Users (authenticated profile operations) ---
        requests.requestMatchers(HttpMethod.PUT, "/api/v1/users/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/users/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        // --- Companies (authenticated profile operations) ---
        requests.requestMatchers(HttpMethod.PUT, "/api/v1/companies/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/v1/companies/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        // --- Admin panel ---
        requests.requestMatchers("/api/v1/admin/**").hasRole(SecurityRoles.ADMIN);
    }
}
