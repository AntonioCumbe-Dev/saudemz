package com.saudemz.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.dto.RegistoPacienteRequest;
import com.saudemz.model.Paciente;
import com.saudemz.model.PerfilUsuario;
import com.saudemz.model.Usuario;
import com.saudemz.repository.PacienteRepository;
import com.saudemz.repository.UsuarioRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteService(
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CADASTRAR PACIENTE E CRIAR CONTA DE ACESSO
    @Transactional
    public Paciente registarPaciente(RegistoPacienteRequest dados) {

        if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Este email já está registado.");
        }

        Usuario usuario = new Usuario(
                dados.getNome(),
                dados.getEmail(),
                passwordEncoder.encode(dados.getSenha()),
                PerfilUsuario.PACIENTE
        );

        usuarioRepository.save(usuario);

        Paciente paciente = new Paciente(
                dados.getNome(),
                dados.getTelefone(),
                dados.getDataNascimento(),
                dados.getEmail(),
                dados.getEndereco(),
                dados.getSexo()
        );

        paciente.setUsuario(usuario);

        return pacienteRepository.save(paciente);
    }

    // LISTAR PACIENTES
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    // BUSCAR PACIENTE PELO ID
    public Optional<Paciente> buscarPacientePorId(Long id) {

        Optional<Paciente> paciente = pacienteRepository.findById(id);

        if (paciente.isPresent()) {
            System.out.println("Paciente encontrado");
        } else {
            System.out.println("Paciente não encontrado");
        }

        return paciente;
    }

    // ACTUALIZAR PACIENTE
    public Paciente actualizarPaciente(
            Long id, Paciente dadosActualizados) {

        Optional<Paciente> paciente = pacienteRepository.findById(id);

        if (paciente.isPresent()) {

            Paciente pacienteExistente = paciente.get();

            pacienteExistente.setTelefone(
                    dadosActualizados.getTelefone());

            return pacienteRepository.save(pacienteExistente);

        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }

    // ELIMINAR PACIENTE
    public void eliminarPaciente(Long id) {

        Optional<Paciente> paciente = pacienteRepository.findById(id);

        if (paciente.isPresent()) {
            pacienteRepository.deleteById(id);
        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }
}