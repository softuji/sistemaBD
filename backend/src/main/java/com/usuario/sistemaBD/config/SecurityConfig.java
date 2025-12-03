package com.usuario.sistemaBD.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF para APIs REST
                .csrf(AbstractHttpConfigurer::disable)

                // Permite todas as requisições (sem autenticação JWT)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Permite tudo
                )

                // Desabilita segurança HTTP básica
                .httpBasic(AbstractHttpConfigurer::disable)

                // Desabilita login form do Spring Security
                .formLogin(AbstractHttpConfigurer::disable)

                // Desabilita logout do Spring Security
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}