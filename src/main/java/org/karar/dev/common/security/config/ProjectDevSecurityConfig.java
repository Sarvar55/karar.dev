package org.karar.dev.common.security.config;

import org.karar.dev.common.security.SecurityPathConfig;
import org.karar.dev.common.security.exception.CustomAccessDeniedHandler;
import org.karar.dev.common.security.exception.CustomAuthenticationEntryPoint;
import org.karar.dev.common.security.filter.AuthenticationTokenFilter;
import org.karar.dev.common.security.provider.CustomAuthenticationProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("dev")
@EnableMethodSecurity
public class ProjectDevSecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final AuthenticationTokenFilter authenticationTokenFilter;
    private final CorsConfiguration corsConfiguration;

    public ProjectDevSecurityConfig(
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler, AuthenticationTokenFilter authenticationTokenFilter, CorsConfiguration corsConfiguration
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationTokenFilter = authenticationTokenFilter;
        this.corsConfiguration = corsConfiguration;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(SecurityPathConfig::configure)
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomAuthenticationProvider provider,
            ApplicationEventPublisher eventPublisher) {

        ProviderManager providerManager = new ProviderManager(List.of(provider));
        
        providerManager.setAuthenticationEventPublisher(
                new DefaultAuthenticationEventPublisher(eventPublisher));
        return providerManager;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedOrigins(Arrays.asList(corsConfiguration.getAllowedOrigins()));
        config.setAllowCredentials(corsConfiguration.isAllowCredentials());
        source.registerCorsConfiguration("/**", config);
        config.setMaxAge(3600L);
        return source;
    }

}

