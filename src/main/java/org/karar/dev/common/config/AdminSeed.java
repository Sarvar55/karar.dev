package org.karar.dev.common.config;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.enums.Role;
import org.karar.dev.domain.user.entity.User;
import org.karar.dev.domain.user.repository.UserRepository;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class AdminSeed implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = new RegularUser();
        admin.setEmail("admin@karar.dev");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);


        userRepository.save(admin);
    }
}
