
package com.saudemz.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saudemz.service.EmailService;

@RestController
public class EmailTesteController {

    private final EmailService emailService;

    public EmailTesteController(EmailService emailService) {
        this.emailService = emailService;
    }

    // VERIFICAR AS PERMISSOES DO UTILIZADOR AUTENTICADO
    @GetMapping("/verificar-perfil")
    public Map<String, Object> verificarPerfil(Authentication authentication) {

        // VERIFICAR SE NÃO EXISTE UMA SESSÃO AUTENTICADA
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            return Map.of(
                "autenticado", false,
                "perfil", "",
                "email", ""
            );
        }

        // OBTER OS DADOS DO UTILIZADOR AUTENTICADO
        String perfil = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return Map.of(
            "autenticado", true,
            "perfil", perfil,
            "email", authentication.getName()
        );
    }

    // TESTAR ENVIO DE EMAIL
    @GetMapping("/teste-email")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public String testarEmail(@RequestParam String destinatario) {

        emailService.enviarEmail(
            destinatario,
            "Teste de email - Saúde+ MZ",
            "Parabéns! O sistema Saúde+ MZ conseguiu enviar um email com sucesso."
        );

        return "Email enviado com sucesso!";
    }
}
