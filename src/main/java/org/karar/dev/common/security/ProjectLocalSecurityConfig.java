package org.karar.dev.common.security;

import org.karar.dev.common.enums.Role;
import org.karar.dev.common.security.exception.CustomAccessDeniedHandler;
import org.karar.dev.common.security.exception.CustomAuthenticationEntryPoint;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.user.regular.RegularUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("local")

public class ProjectLocalSecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public ProjectLocalSecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Local development focus
                .authorizeHttpRequests((requests) -> requests
                        // Auth & Swagger (Public)
                        .requestMatchers("/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Decisions (List and Get are public, modifications are secured)
                        .requestMatchers(HttpMethod.GET, "/api/v1/decisions/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/decisions").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/decisions/**").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/decisions/**").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())

                        // Tags (Public read, Admin only write)
                        .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
                        .requestMatchers("/api/v1/tags/**").hasRole(Role.ADMIN.name())

                        // Comments (Read public, Write secured)
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                        .requestMatchers("/api/v1/comments/**").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())

                        // Votes (Public check/count, Secured voting/deleting)
                        .requestMatchers(HttpMethod.GET, "/api/v1/votes/check", "/api/v1/votes/decision/**").permitAll()
                        .requestMatchers("/api/v1/votes/**").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())

                        // Users
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasAnyRole(Role.USER.name(), Role.COMPANY.name(), Role.ADMIN.name())

                        // Base Admin only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole(Role.ADMIN.name())

                        .anyRequest().authenticated()
                );

        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        http.exceptionHandling(exh -> exh.authenticationEntryPoint(authenticationEntryPoint));
        http.exceptionHandling(eadexh -> eadexh.accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = SecurityUser
                .builder()
                .user(new RegularUser("user", "{noop}12345", Role.USER))
                .build();

        UserDetails admin = SecurityUser
                .builder()
                .user(new RegularUser("admin", "{noop}admin", Role.ADMIN))
                .build();

        UserDetails company = SecurityUser
                .builder()
                .user(new RegularUser("company", "{noop}company", Role.COMPANY))
                .build();

        return new InMemoryUserDetailsManager(user, admin, company);
    }
}
