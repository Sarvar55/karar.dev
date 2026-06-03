package org.karar.dev.common.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

public final class SecurityPathConfig {

    private SecurityPathConfig() {

    }

    public static void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {
        configurePublicPaths(requests);
        configureSecuredPaths(requests);
        requests.anyRequest().authenticated();
    }

    private static void configurePublicPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {

        requests.requestMatchers("/api/auth/**").permitAll();

        requests.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();

        requests.requestMatchers("/h2-console/**").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/decisions").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/decisions/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/decisions/{decisionId}/comments").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/decisions/{decisionId}/tags").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/tags").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/tags/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/tags/name/{name}").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/comments").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/comments/{id}").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/votes").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/votes/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/votes/check").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/votes/decisions/{decisionId}/count").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/users").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/users/{userId}/comments").permitAll();

        requests.requestMatchers(HttpMethod.GET, "/api/companies").permitAll();
        requests.requestMatchers(HttpMethod.GET, "/api/companies/{id}").permitAll();
    }

    private static void configureSecuredPaths(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry requests) {

        requests.requestMatchers(HttpMethod.POST, "/api/decisions").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.PUT, "/api/decisions/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/decisions/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        requests.requestMatchers(HttpMethod.POST, "/api/tags").hasRole(SecurityRoles.ADMIN);
        requests.requestMatchers(HttpMethod.PUT, "/api/tags/{id}").hasRole(SecurityRoles.ADMIN);
        requests.requestMatchers(HttpMethod.DELETE, "/api/tags/{id}").hasRole(SecurityRoles.ADMIN);

        requests.requestMatchers(HttpMethod.POST, "/api/comments").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.PUT, "/api/comments/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/comments/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        requests.requestMatchers(HttpMethod.POST, "/api/votes").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/votes/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/votes/users/{userId}/decisions/{decisionId}")
                .hasAnyRole(SecurityRoles.ALL_USERS);

        requests.requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        requests.requestMatchers(HttpMethod.PUT, "/api/companies/{id}").hasAnyRole(SecurityRoles.ALL_USERS);
        requests.requestMatchers(HttpMethod.DELETE, "/api/companies/{id}").hasAnyRole(SecurityRoles.ALL_USERS);

        requests.requestMatchers("/api/audit-logs/**").hasRole(SecurityRoles.ADMIN);

        requests.requestMatchers("/api/admin/**").hasRole(SecurityRoles.ADMIN);
    }
}

