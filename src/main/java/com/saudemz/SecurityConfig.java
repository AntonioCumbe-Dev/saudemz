
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

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(usuarioDetailsService);

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

                // Recursos públicos
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/img/**"
                ).permitAll()

                // Listagem pública de médicos
                .requestMatchers(
                    HttpMethod.GET, "/medicos"
                ).permitAll()

                // Consulta pública de disponibilidades
                .requestMatchers(
                    HttpMethod.GET, "/disponibilidades/**"
                ).permitAll()

                // Cadastro público de pacientes
                .requestMatchers(
                    HttpMethod.POST, "/pacientes/cadastro"
                ).permitAll()

                // Obtenção do token CSRF
                .requestMatchers(
                    HttpMethod.GET, "/csrf"
                ).permitAll()

                // Verificação de perfil
                .requestMatchers(
                    HttpMethod.GET, "/verificar-perfil"
                ).permitAll()

                // Restantes operações exigem autenticação
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
                .permitAll()
            );

        return http.build();
    }
}
