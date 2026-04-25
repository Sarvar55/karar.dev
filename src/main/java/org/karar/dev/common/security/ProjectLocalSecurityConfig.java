package org.karar.dev.common.security;

import org.karar.dev.common.enums.Role;
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

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Local development focus
                .authorizeHttpRequests((requests) -> requests
                        // Auth & Swagger (Public)
                        .requestMatchers("/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Decisions (List and Get are public, modifications are secured)
                        .requestMatchers(HttpMethod.GET, "/api/v1/decisions/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/decisions").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/decisions/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/decisions/**").authenticated()

                        // Tags (Public read, Secured write)
                        .requestMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tags/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tags/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tags/**").authenticated()

                        // Comments (Read public, Write secured)
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/**").authenticated()

                        // Votes (Public check/count, Secured voting/deleting)
                        .requestMatchers(HttpMethod.GET, "/api/v1/votes/check", "/api/v1/votes/decision/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/votes").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/votes/**").authenticated()

                        // Users (Profile management usually secured, but creating/listing might vary)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .requestMatchers("/api/v1/users/**").authenticated()

                        .anyRequest().authenticated()
                );

        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = SecurityUser
                .builder()
                .user(new RegularUser("sarvar", "{noop}12345", Role.USER))
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
