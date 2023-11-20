package com.springboot.chatgptbot.controller.auth;

import com.springboot.chatgptbot.service.web.UserDetailsServiceImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String redirectTo() {
        boolean isAuthenticated = checkAuthenticationStatus();
        if (isAuthenticated) {
            return "redirect:/openai/list";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username, @RequestParam String password, Model model) {

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(username, password));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return "redirect:/openai/list";
            } else {
                model.addAttribute("error", "Incorrect login or password.");
                return "login";
            }
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", "User not found.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext();
        }
        return "redirect:/login";
    }

    private boolean checkAuthenticationStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }
}