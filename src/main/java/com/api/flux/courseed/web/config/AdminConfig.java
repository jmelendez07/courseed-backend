package com.api.flux.courseed.web.config;

import java.util.Arrays;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.api.flux.courseed.persistence.documents.User;
import com.api.flux.courseed.persistence.repositories.UserRepository;
import com.api.flux.courseed.services.interfaces.Roles;

@Component
public class AdminConfig implements CommandLineRunner {
    
    @Lazy
    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    // @Value("${ADMIN_EMAIL}")
    private String adminEmail = "admin@admin.com";

    // @Value("${ADMIN_PASS}")
    private String adminPass = "test12345";

    @Override
    public void run(String... args) {
        User user = new User(adminEmail, passwordEncoder.encode(adminPass), Arrays.asList(Roles.PREFIX + Roles.ADMIN));

        userRepository.findByEmail(user.getEmail())
            .switchIfEmpty(userRepository.save(user))
            .then().subscribe();
    }
}