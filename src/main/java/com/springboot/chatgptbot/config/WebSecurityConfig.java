package com.springboot.chatgptbot.config;

import com.springboot.chatgptbot.repository.UserRepository;
import com.springboot.chatgptbot.service.web.AdminInitializationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/", "/register", "/login").permitAll();
                    authorize.requestMatchers("/openai/list").authenticated();
                    authorize.anyRequest().authenticated();
                })
                .formLogin(login -> {
                    login.loginPage("/login");
                    login.defaultSuccessUrl("/openai/list");
                })
                .logout(logout -> {
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
                    logout.logoutSuccessUrl("/login");
                    logout.deleteCookies("JSESSIONID");
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AdminInitializationService myAdminInitializationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new AdminInitializationService(userRepository, passwordEncoder);
    }
}
