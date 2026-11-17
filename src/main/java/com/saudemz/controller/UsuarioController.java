package com.saudemz.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // IDENTIFICAR O UTILIZADOR AUTENTICADO
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> utilizadorAutenticado(
            Authentication authentication) {

        String perfil = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(autoridade -> autoridade.startsWith("ROLE_"))
                .map(autoridade -> autoridade.substring(5))
                .findFirst()
                .orElse("SEM_PERFIL");

        Map<String, Object> resposta = Map.of(
                "email", authentication.getName(),
                "perfil", perfil
        );

        return ResponseEntity.ok(resposta);
    }
}