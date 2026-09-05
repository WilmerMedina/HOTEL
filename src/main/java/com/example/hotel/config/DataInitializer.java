package com.example.hotel.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.hotel.entity.User;
import com.example.hotel.enums.Role;
import com.example.hotel.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@hotel.com").isEmpty()) {

            User admin = new User();

            admin.setName("Administrador");
            admin.setEmail("admin@hotel.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setLocked(false);

            userRepository.save(admin);
        }

    }
}