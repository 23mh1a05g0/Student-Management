package com.student.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ❌ Disable CSRF (for REST APIs)
            .csrf(csrf -> csrf.disable())

            // ✅ Allow frontend + auth APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/signup",
                        "/dashboard",
                        "/style.css",
                        "/script.js",
                        "/auth/**"
                ).permitAll()

                // 🔒 Any other API (future: /students/**) can be secured
                .anyRequest().authenticated()
            )

            // ❌ Disable default login page
            .formLogin(form -> form.disable())

            // ❌ Disable HTTP Basic auth
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // 🔐 Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}