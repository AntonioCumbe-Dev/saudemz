
package com.saudemz.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.Medico;
import com.saudemz.model.PerfilUsuario;
import com.saudemz.model.Usuario;
import com.saudemz.repository.MedicoRepository;
import com.saudemz.repository.UsuarioRepository;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(
            MedicoRepository medicoRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registar médico e criar conta automaticamente
    @Transactional
    public Medico salvarDados(Medico medico, String senha) {

        if (medico.getNome() == null || medico.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }

        if (medico.getEmail() == null || medico.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }

        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres.");
        }

        String email = medico.getEmail().trim().toLowerCase();

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe uma conta com este email.");
        }

        medico.setEmail(email);

        Usuario usuario = new Usuario();
        usuario.setNome(medico.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.MEDICO);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        medico.setUsuario(usuario);

        return medicoRepository.save(medico);
    }

    // Listar todos os médicos
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    // Buscar médico pelo ID
    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médico não encontrado."));
    }

    // Encontrar médico através do email da conta
    public Medico buscarPorEmailUtilizador(String email) {
        return medicoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não existe médico associado a esta conta."));
    }

    // Associar conta de utilizador a um médico
    @Transactional
    public Medico associarUtilizador(Long medicoId, Long usuarioId) {

        Medico medico = buscarPorId(medicoId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilizador não encontrado."));

        if (medico.getUsuario() != null) {
            throw new IllegalArgumentException(
                    "Este médico já possui uma conta associada.");
        }

        if (usuario.getPerfil() != PerfilUsuario.MEDICO) {
            throw new IllegalArgumentException(
                    "A conta seleccionada não possui o perfil de médico.");
        }

        if (medicoRepository.findByUsuarioEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Esta conta já está associada a outro médico.");
        }

        medico.setUsuario(usuario);

        return medicoRepository.save(medico);
    }

    // Actualizar médico
    public Medico actualizar(Long id, Medico dados) {

        Medico medicoExistente = buscarPorId(id);

        medicoExistente.setNome(dados.getNome());
        medicoExistente.setEspecialidade(dados.getEspecialidade());
        medicoExistente.setTelefone(dados.getTelefone());
        medicoExistente.setEmail(dados.getEmail());

        return medicoRepository.save(medicoExistente);
    }

    // Eliminar médico
    public void eliminar(Long id) {
        Medico medico = buscarPorId(id);
        medicoRepository.delete(medico);
    }
}
