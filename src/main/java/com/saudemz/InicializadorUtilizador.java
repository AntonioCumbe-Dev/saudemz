
package com.saudemz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saudemz.model.PerfilUsuario;
import com.saudemz.model.Usuario;
import com.saudemz.repository.UsuarioRepository;

@Configuration
public class InicializadorUtilizador {

    @Bean
    public CommandLineRunner criarSuperutilizador(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "admin@saudemz.co.mz";

            if (usuarioRepository.findByEmail(email).isEmpty()) {

                String senha = System.getenv("SAUDEMZ_ADMIN_PASSWORD");

                if (senha == null || senha.isBlank()) {
                    System.out.println(
                        "Superutilizador não criado: configure SAUDEMZ_ADMIN_PASSWORD."
                    );
                    return;
                }

                Usuario usuario = new Usuario(
                    "Administrador",
                    email,
                    passwordEncoder.encode(senha),
                    PerfilUsuario.SUPERUTILIZADOR
                );

                usuario.setAtivo(true);
                usuarioRepository.save(usuario);

                System.out.println("Superutilizador criado com sucesso.");

            } else {
                System.out.println("Conta do superutilizador já existente.");
            }
        };
    }
}
