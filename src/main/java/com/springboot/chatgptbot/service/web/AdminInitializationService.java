package com.springboot.chatgptbot.service.web;

import com.springboot.chatgptbot.domain.User;
import com.springboot.chatgptbot.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminInitializationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private boolean adminInitialized = false;

    public AdminInitializationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostConstruct
    public void initializeAdminUser() {
        if (!adminInitialized && !userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(adminUser);
            adminInitialized = true;
        }
    }
}