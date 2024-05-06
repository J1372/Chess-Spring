package com.github.J1372.WebBackend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("", "/", "/home", "/login", "/create-account").permitAll()

                        .requestMatchers(HttpMethod.POST, "/users", "/login/process").anonymous()
                        .requestMatchers("/username").permitAll()
                        .requestMatchers("/users/*").permitAll()
                        .requestMatchers("/users/*/stats").permitAll()
                        .requestMatchers("/users/*/recent-games").permitAll()
                        .requestMatchers( "/users/*/history/*").permitAll()

                        .requestMatchers(HttpMethod.POST, "/games").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/games/*").authenticated()
                        .requestMatchers("/games/open-games").permitAll()
                        .requestMatchers("/games/*").permitAll()

                        .anyRequest().denyAll()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                )
                .build();
    }
}
