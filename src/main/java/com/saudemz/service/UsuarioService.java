package com.saudemz.service;

import org.springframework.stereotype.Service;

import com.saudemz.model.Usuario;
import com.saudemz.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // BUSCAR UTILIZADOR PELO EMAIL
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElse(null);
    }
}