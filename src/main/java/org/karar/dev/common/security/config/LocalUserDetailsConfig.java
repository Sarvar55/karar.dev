package org.karar.dev.common.security.config;

import org.karar.dev.common.enums.Role;
import org.karar.dev.common.security.user.SecurityUser;
import org.karar.dev.domain.user.company.entity.CompanyUser;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@Profile("local")
public class LocalUserDetailsConfig {

    @Bean
    public UserDetailsManager userDetailsService() {
        RegularUser regularUser = new RegularUser("user", "{noop}12345", Role.USER);
        CompanyUser companyUser = new CompanyUser("company", "{noop}company", "Company Name");
        RegularUser regularAdmin = new RegularUser("admin", "{noop}admin", Role.ADMIN);

        UserDetails user = new SecurityUser(regularUser);
        UserDetails admin = new SecurityUser(regularAdmin);
        UserDetails company = new SecurityUser(companyUser);

        return new InMemoryUserDetailsManager(user, admin, company);
    }
}

