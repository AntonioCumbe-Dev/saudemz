package com.saudemz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.saudemz.service.UsuarioDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UsuarioDetailsService usuarioDetailsService) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider)
            throws Exception {

        http
                .authenticationProvider(authenticationProvider)

                .csrf(csrf -> csrf.spa())

                .authorizeHttpRequests(auth -> auth

                        // 1. Páginas e recursos públicos
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/images/**")
                        .permitAll()

                        // 2. Listagem pública de médicos (landing page)
                        .requestMatchers(
                                HttpMethod.GET, "/medicos")
                        .permitAll()

                        // 3. Disponibilidades públicas (necessário para mostrar horários antes do
                        // login)
                        .requestMatchers(
                                HttpMethod.GET, "/disponibilidades/**")
                        .permitAll()

                        // 4. Registo público de pacientes
                        .requestMatchers(
                                HttpMethod.POST, "/pacientes/cadastro")
                        .permitAll()

                        // 5. Token CSRF e verificação de perfil
                        .requestMatchers(
                                HttpMethod.GET, "/csrf")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.GET, "/verificar-perfil")
                        .permitAll()

                        // 6. Restantes recursos protegidos
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll());

        return http.build();
    }
}