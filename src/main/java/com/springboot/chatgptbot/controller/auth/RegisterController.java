package com.springboot.chatgptbot.controller.auth;

import com.springboot.chatgptbot.domain.User;
import com.springboot.chatgptbot.exception.UserAlreadyExistException;
import com.springboot.chatgptbot.repository.UserRepository;
import com.springboot.chatgptbot.service.web.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    private final UserDetailsServiceImpl userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserDetailsServiceImpl userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) throws UserAlreadyExistException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "/register";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameExistsError", "Username is already taken.");
            return "/register";
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User newUser = new User(user.getUsername(), encodedPassword);
        userService.registerUser(newUser);

        return "redirect:/login";
    }
}