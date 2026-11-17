
package com.saudemz.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.Consulta;
import com.saudemz.model.DisponibilidadeMedico;
import com.saudemz.model.Medico;
import com.saudemz.model.Paciente;
import com.saudemz.model.PerfilUsuario;
import com.saudemz.model.SequenciaConsulta;
import com.saudemz.model.Usuario;

import com.saudemz.repository.ConsultaRepository;
import com.saudemz.repository.DisponibilidadeMedicoRepository;
import com.saudemz.repository.MedicoRepository;
import com.saudemz.repository.PacienteRepository;
import com.saudemz.repository.SequenciaConsultaRepository;

@Service
@Transactional
public class ConsultaService {

    private static final Logger log =
            LoggerFactory.getLogger(ConsultaService.class);

    private static final int DURACAO_CONSULTA = 30;

    private static final LocalTime HORA_ABERTURA =
            LocalTime.of(6, 0);

    private static final LocalTime HORA_FECHO =
            LocalTime.of(20, 0);

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final SequenciaConsultaRepository sequenciaRepository;
    private final DisponibilidadeMedicoRepository disponibilidadeRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            SequenciaConsultaRepository sequenciaRepository,
            DisponibilidadeMedicoRepository disponibilidadeRepository,
            UsuarioService usuarioService,
            EmailService emailService) {

        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.sequenciaRepository = sequenciaRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    // ==========================================
    // IDENTIFICAR UTILIZADOR AUTENTICADO
    // ==========================================

    private Usuario obterUtilizadorAutenticado() {

        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null
                || !autenticacao.isAuthenticated()
                || "anonymousUser".equals(autenticacao.getName())) {

            throw new IllegalArgumentException(
                    "É necessário iniciar sessão.");
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(autenticacao.getName());

        if (usuario == null || !usuario.isAtivo()) {
            throw new IllegalArgumentException(
                    "Utilizador não encontrado ou inactivo.");
        }

        return usuario;
    }

    private Paciente obterPacienteAutenticado() {

        Usuario usuario = obterUtilizadorAutenticado();

        return pacienteRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não foi encontrado um paciente associado à conta."));
    }

    private Medico obterMedicoAutenticado() {

        Usuario usuario = obterUtilizadorAutenticado();

        return medicoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não foi encontrado um médico associado à conta."));
    }

    // ==========================================
    // GERAR CÓDIGO ÚNICO DA CONSULTA
    // ==========================================

    private String gerarCodigoConsulta() {

        LocalDate hoje = LocalDate.now();

        String dataCodigo = String.format(
                "%02d%02d",
                hoje.getDayOfMonth(),
                hoje.getMonthValue());

        SequenciaConsulta sequencia =
                sequenciaRepository
                        .buscarPorDataComBloqueio(dataCodigo)
                        .orElseGet(() ->
                                new SequenciaConsulta(dataCodigo, 0));

        sequencia.setUltimoNumero(
                sequencia.getUltimoNumero() + 1);

        sequenciaRepository.saveAndFlush(sequencia);

        return "SAUDEMZ-" + dataCodigo
                + String.format("%03d", sequencia.getUltimoNumero());
    }

    // ==========================================
    // VALIDAR DATA E HORA
    // ==========================================

    private void validarDataHora(LocalDateTime dataHora) {

        if (dataHora == null) {
            throw new IllegalArgumentException(
                    "A data e a hora da consulta são obrigatórias.");
        }

        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Não é possível marcar uma consulta no passado.");
        }

        LocalTime hora = dataHora.toLocalTime();

        if (hora.isBefore(HORA_ABERTURA)
                || hora.plusMinutes(DURACAO_CONSULTA)
                        .isAfter(HORA_FECHO)) {

            throw new IllegalArgumentException(
                    "O horário de atendimento é das 06:00 às 20:00.");
        }

        if (hora.getMinute() != 0 && hora.getMinute() != 30) {
            throw new IllegalArgumentException(
                    "As consultas devem começar à hora certa "
                    + "ou à meia hora.");
        }

        if (hora.getSecond() != 0 || hora.getNano() != 0) {
            throw new IllegalArgumentException(
                    "O horário indicado não é válido.");
        }
    }

    // ==========================================
    // VALIDAR DISPONIBILIDADE DO MÉDICO
    // ==========================================

    private void validarDisponibilidadeMedico(
            Long medicoId,
            LocalDateTime inicio) {

        DayOfWeek dia = inicio.getDayOfWeek();

        LocalTime horaInicio = inicio.toLocalTime();

        LocalTime horaFim =
                horaInicio.plusMinutes(DURACAO_CONSULTA);

        List<DisponibilidadeMedico> horarios =
                disponibilidadeRepository
                        .findByMedicoIdAndDiaSemanaAndDisponivelTrue(
                                medicoId, dia);

        boolean horarioEncontrado = horarios.stream().anyMatch(
                disponibilidade ->
                        !horaInicio.isBefore(
                                disponibilidade.getHoraInicio())
                        && !horaFim.isAfter(
                                disponibilidade.getHoraFim()));

        if (!horarioEncontrado) {
            throw new IllegalArgumentException(
                    "O médico não está disponível neste dia e horário.");
        }
    }

    // ==========================================
    // VALIDAR CONFLITO DO MÉDICO
    // ==========================================

    private void validarConflitoMedico(
            Long medicoId,
            LocalDateTime inicio) {

        LocalDateTime inicioMenos30 =
                inicio.minusMinutes(DURACAO_CONSULTA);

        LocalDateTime fim =
                inicio.plusMinutes(DURACAO_CONSULTA);

        List<Consulta> conflitos =
                consultaRepository.buscarConflitosDeHorario(
                        medicoId, inicioMenos30, fim);

        if (!conflitos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Este médico já tem uma consulta marcada "
                    + "neste horário.");
        }
    }

    // ==========================================
    // VALIDAR CONFLITO DO PACIENTE
    // ==========================================

    private void validarConflitoPaciente(
            Long pacienteId,
            LocalDateTime inicio) {

        LocalDateTime inicioMenos30 =
                inicio.minusMinutes(DURACAO_CONSULTA);

        LocalDateTime fim =
                inicio.plusMinutes(DURACAO_CONSULTA);

        List<Consulta> conflitos =
                consultaRepository.buscarConflitosDoPaciente(
                        pacienteId, inicioMenos30, fim);

        if (!conflitos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Já possui uma consulta marcada neste horário.");
        }
    }

    // ==========================================
    // MARCAR CONSULTA PELO ADMINISTRADOR
    // ==========================================

    public Consulta marcarConsulta(Consulta dados) {

        if (dados.getPaciente() == null
                || dados.getPaciente().getId() == null) {

            throw new IllegalArgumentException(
                    "Seleccione um paciente.");
        }

        if (dados.getMedico() == null
                || dados.getMedico().getId() == null) {

            throw new IllegalArgumentException(
                    "Seleccione um médico.");
        }

        Paciente paciente =
                pacienteRepository.findById(
                        dados.getPaciente().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Paciente não encontrado."));

        Medico medico =
                medicoRepository.findById(
                        dados.getMedico().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Médico não encontrado."));

        LocalDateTime dataHora = dados.getDataHora();

        validarDataHora(dataHora);

        validarDisponibilidadeMedico(medico.getId(), dataHora);

        validarConflitoMedico(medico.getId(), dataHora);

        validarConflitoPaciente(paciente.getId(), dataHora);

        Consulta consulta = new Consulta();

        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setDataHora(dataHora);
        consulta.setMotivo(dados.getMotivo());
        consulta.setEstado("MARCADA");
        consulta.setCodigoConsulta(gerarCodigoConsulta());

        Consulta guardada = consultaRepository.save(consulta);

        notificarNovaConsulta(guardada);

        return guardada;
    }

    // ==========================================
    // MARCAR CONSULTA PELO PACIENTE AUTENTICADO
    // ==========================================

    public Consulta marcarConsultaPaciente(Consulta dados) {

        Paciente paciente = obterPacienteAutenticado();

        if (dados.getMedico() == null
                || dados.getMedico().getId() == null) {

            throw new IllegalArgumentException(
                    "Seleccione um médico.");
        }

        Medico medico =
                medicoRepository.findById(
                        dados.getMedico().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Médico não encontrado."));

        LocalDateTime dataHora = dados.getDataHora();

        validarDataHora(dataHora);

        validarDisponibilidadeMedico(medico.getId(), dataHora);

        validarConflitoMedico(medico.getId(), dataHora);

        validarConflitoPaciente(paciente.getId(), dataHora);

        Consulta consulta = new Consulta();

        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setDataHora(dataHora);
        consulta.setMotivo(dados.getMotivo());
        consulta.setEstado("MARCADA");
        consulta.setCodigoConsulta(gerarCodigoConsulta());

        Consulta guardada = consultaRepository.save(consulta);

        notificarNovaConsulta(guardada);

        return guardada;
    }

    // ==========================================
    // NOTIFICAR NOVA CONSULTA
    // ==========================================

    private void notificarNovaConsulta(Consulta consulta) {

        try {
            String email = consulta.getPaciente().getEmail();

            if (email == null || email.isBlank()) {
                return;
            }

            String mensagem =
                    "Olá, " + consulta.getPaciente().getNome() + "!\n\n"
                    + "A sua consulta foi marcada com sucesso.\n\n"
                    + "Código: " + consulta.getCodigoConsulta() + "\n"
                    + "Médico: " + consulta.getMedico().getNome() + "\n"
                    + "Especialidade: "
                    + consulta.getMedico().getEspecialidade() + "\n"
                    + "Data e hora: " + consulta.getDataHora() + "\n\n"
                    + "Saúde+ MZ - Cuidar de si é a nossa prioridade.";

            emailService.enviarEmail(
                    email,
                    "Consulta marcada - Saúde+ MZ",
                    mensagem);

        } catch (RuntimeException erro) {
            log.warn("Não foi possível enviar o email da consulta: {}",
                    erro.getMessage());
        }
    }

    // ==========================================
    // LISTAR CONSULTAS CONFORME O PERFIL
    // ==========================================

    @Transactional(readOnly = true)
    public List<Consulta> listarTodas() {

        Usuario usuario = obterUtilizadorAutenticado();

        if (usuario.getPerfil() == PerfilUsuario.SUPERUTILIZADOR) {
            return consultaRepository.findAll();
        }

        if (usuario.getPerfil() == PerfilUsuario.MEDICO) {

            Medico medico = obterMedicoAutenticado();

            return consultaRepository.findByMedicoId(medico.getId());
        }

        if (usuario.getPerfil() == PerfilUsuario.PACIENTE) {

            Paciente paciente = obterPacienteAutenticado();

            return consultaRepository.findByPacienteId(
                    paciente.getId());
        }

        throw new IllegalArgumentException(
                "Perfil de utilizador não reconhecido.");
    }

    // ==========================================
    // CONSULTAS DO DIA
    // ==========================================

    @Transactional(readOnly = true)
    public List<Consulta> listarConsultasDoDia() {

        LocalDate hoje = LocalDate.now();

        LocalDateTime inicio = hoje.atStartOfDay();

        LocalDateTime fim = hoje.plusDays(1).atStartOfDay()
                .minusNanos(1);

        return consultaRepository.buscarConsultasDoDia(inicio, fim);
    }

    // ==========================================
    // BUSCAR CONSULTA POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public Consulta buscarPorId(Long id) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Consulta não encontrada."));

        validarAcessoConsulta(consulta);

        return consulta;
    }

    // ==========================================
    // BUSCAR CONSULTA PELO CÓDIGO
    // ==========================================

    @Transactional(readOnly = true)
    public Consulta buscarPorCodigoConsulta(String codigo) {

        Consulta consulta =
                consultaRepository.findByCodigoConsulta(codigo)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Não foi encontrada uma consulta "
                                        + "com esse código."));

        validarAcessoConsulta(consulta);

        return consulta;
    }

    // ==========================================
    // VALIDAR ACESSO À CONSULTA
    // ==========================================

    private void validarAcessoConsulta(Consulta consulta) {

        Usuario usuario = obterUtilizadorAutenticado();

        if (usuario.getPerfil() == PerfilUsuario.SUPERUTILIZADOR) {
            return;
        }

        if (usuario.getPerfil() == PerfilUsuario.PACIENTE) {

            Paciente paciente = obterPacienteAutenticado();

            if (!consulta.getPaciente().getId()
                    .equals(paciente.getId())) {

                throw new IllegalArgumentException(
                        "Não tem autorização para consultar este registo.");
            }

            return;
        }

        if (usuario.getPerfil() == PerfilUsuario.MEDICO) {

            Medico medico = obterMedicoAutenticado();

            if (!consulta.getMedico().getId()
                    .equals(medico.getId())) {

                throw new IllegalArgumentException(
                        "Não tem autorização para consultar este registo.");
            }

            return;
        }

        throw new IllegalArgumentException(
                "Não tem autorização para consultar este registo.");
    }

    // ==========================================
    // LISTAR HORÁRIOS OCUPADOS DE UM MÉDICO
    // ==========================================

    @Transactional(readOnly = true)
    public List<LocalTime> listarHorariosOcupados(
            Long medicoId,
            LocalDate data) {

        if (data == null) {
            throw new IllegalArgumentException(
                    "Indique a data da consulta.");
        }

        if (!medicoRepository.existsById(medicoId)) {
            throw new IllegalArgumentException(
                    "Médico não encontrado.");
        }

        LocalDateTime inicio = data.atStartOfDay();

        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        return consultaRepository.buscarHorariosOcupados(
                        medicoId, inicio, fim)
                .stream()
                .map(Consulta::getDataHora)
                .map(LocalDateTime::toLocalTime)
                .toList();
    }

    // ==========================================
    // REAGENDAR CONSULTA
    // ==========================================

    public Consulta reagendarConsulta(
            Long id,
            Consulta novosDados) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Consulta não encontrada."));

        validarAcessoConsulta(consulta);

        Usuario usuario = obterUtilizadorAutenticado();

        if (consulta.getEstado().equals("CANCELADA")
                || consulta.getEstado().equals("CONCLUIDA")) {

            throw new IllegalArgumentException(
                    "Não é possível reagendar uma consulta "
                    + "cancelada ou concluída.");
        }

        LocalDateTime novaDataHora = novosDados.getDataHora();

        validarDataHora(novaDataHora);

        Medico novoMedico = consulta.getMedico();

        if (novosDados.getMedico() != null
                && novosDados.getMedico().getId() != null) {

            novoMedico = medicoRepository.findById(
                    novosDados.getMedico().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Médico não encontrado."));
        }

        validarDisponibilidadeMedico(
                novoMedico.getId(), novaDataHora);

        LocalDateTime inicioMenos30 =
                novaDataHora.minusMinutes(DURACAO_CONSULTA);

        LocalDateTime fim =
                novaDataHora.plusMinutes(DURACAO_CONSULTA);

        if (!consultaRepository
                .buscarConflitosDeHorarioExcluindoConsulta(
                        novoMedico.getId(),
                        consulta.getId(),
                        inicioMenos30,
                        fim).isEmpty()) {

            throw new IllegalArgumentException(
                    "O médico já tem uma consulta neste horário.");
        }

        if (!consultaRepository
                .buscarConflitosDoPacienteExcluindoConsulta(
                        consulta.getPaciente().getId(),
                        consulta.getId(),
                        inicioMenos30,
                        fim).isEmpty()) {

            throw new IllegalArgumentException(
                    "Já existe outra consulta deste paciente "
                    + "neste horário.");
        }

        consulta.setMedico(novoMedico);
        consulta.setDataHora(novaDataHora);

        Consulta actualizada = consultaRepository.save(consulta);

        try {
            emailService.notificarConsultaReagendada(
                    consulta.getPaciente().getEmail(),
                    consulta.getPaciente().getNome(),
                    novoMedico.getNome(),
                    novaDataHora.toString());
        } catch (RuntimeException erro) {
            log.warn("Falha ao notificar reagendamento: {}",
                    erro.getMessage());
        }

        return actualizada;
    }

    // ==========================================
    // ACTUALIZAR ESTADO DA CONSULTA
    // ==========================================

    public Consulta actualizarEstado(Long id, String novoEstado) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Consulta não encontrada."));

        validarAcessoConsulta(consulta);

        Usuario usuario = obterUtilizadorAutenticado();

        if (!"MARCADA".equals(consulta.getEstado())) {
            throw new IllegalArgumentException(
                    "Só é possível alterar o estado de consultas marcadas.");
        }

        if (!"CANCELADA".equals(novoEstado)
                && !"CONCLUIDA".equals(novoEstado)) {

            throw new IllegalArgumentException(
                    "Estado inválido. Utilize CANCELADA ou CONCLUIDA.");
        }

        if (usuario.getPerfil() == PerfilUsuario.PACIENTE
                && !"CANCELADA".equals(novoEstado)) {

            throw new IllegalArgumentException(
                    "O paciente só pode cancelar a sua consulta.");
        }

        if (usuario.getPerfil() == PerfilUsuario.MEDICO
                && !"CONCLUIDA".equals(novoEstado)) {

            throw new IllegalArgumentException(
                    "O médico só pode concluir as suas consultas.");
        }

        consulta.setEstado(novoEstado);

        Consulta actualizada = consultaRepository.save(consulta);

        try {
            if ("CANCELADA".equals(novoEstado)) {

                emailService.notificarConsultaCancelada(
                        consulta.getPaciente().getEmail(),
                        consulta.getPaciente().getNome(),
                        consulta.getMedico().getNome());

            } else if ("CONCLUIDA".equals(novoEstado)) {

                emailService.notificarConsultaConcluida(
                        consulta.getPaciente().getEmail(),
                        consulta.getPaciente().getNome(),
                        consulta.getMedico().getNome());
            }

        } catch (RuntimeException erro) {
            log.warn("Falha ao enviar notificação de alteração: {}",
                    erro.getMessage());
        }

        return actualizada;
    }
}
