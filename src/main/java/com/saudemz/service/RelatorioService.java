
package com.saudemz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.Consulta;
import com.saudemz.model.Paciente;
import com.saudemz.model.Usuario;
import com.saudemz.model.Medico;
import com.saudemz.repository.ConsultaRepository;
import com.saudemz.repository.MedicoRepository;
import com.saudemz.repository.PacienteRepository;

@Service
@Transactional(readOnly = true)
public class RelatorioService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioService usuarioService;

    public RelatorioService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository,
            UsuarioService usuarioService) {

        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioService = usuarioService;
    }

    // =====================================================
    // RELATÓRIO DE CONSULTAS POR ESTADO
    // =====================================================

    public Map<String, Object> gerarRelatorioPorEstado() {

        long totalMarcadas =
                consultaRepository.countByEstado("MARCADA");

        long totalConcluidas =
                consultaRepository.countByEstado("CONCLUIDA");

        long totalCanceladas =
                consultaRepository.countByEstado("CANCELADA");

        long totalConsultas = consultaRepository.count();

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("totalConsultas", totalConsultas);
        relatorio.put("marcadas", totalMarcadas);
        relatorio.put("concluidas", totalConcluidas);
        relatorio.put("canceladas", totalCanceladas);

        relatorio.put("percentagemMarcadas",
                calcularPercentagem(totalMarcadas, totalConsultas));

        relatorio.put("percentagemConcluidas",
                calcularPercentagem(totalConcluidas, totalConsultas));

        relatorio.put("percentagemCanceladas",
                calcularPercentagem(totalCanceladas, totalConsultas));

        return relatorio;
    }

    // =====================================================
    // RELATÓRIO DE CONSULTAS POR PERÍODO
    // =====================================================

    public Map<String, Object> gerarRelatorioPorPeriodo(
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        validarPeriodo(dataInicio, dataFim);

        List<Consulta> consultas =
                consultaRepository.findByDataHoraBetween(
                        dataInicio, dataFim);

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("dataInicio", dataInicio);
        relatorio.put("dataFim", dataFim);
        relatorio.put("totalConsultas", consultas.size());
        relatorio.put("consultas", consultas);

        return relatorio;
    }

    // =====================================================
    // RELATÓRIO DE CONSULTAS POR MÉDICO
    // =====================================================

    public Map<String, Object> gerarRelatorioPorMedico(Long medicoId) {

        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador do médico é inválido.");
        }

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médico não encontrado."));

        List<Consulta> consultas =
                consultaRepository.findByMedicoId(medicoId);

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("medicoId", medicoId);
        relatorio.put("medico", medico.getNome());
        relatorio.put("especialidade", medico.getEspecialidade());
        relatorio.put("totalConsultas", consultas.size());
        relatorio.put("consultas", consultas);

        return relatorio;
    }

    // =====================================================
    // RELATÓRIO DE CONSULTAS POR ESPECIALIDADE
    // =====================================================

    public Map<String, Object> gerarRelatorioPorEspecialidade(
            String especialidade) {

        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException(
                    "A especialidade é obrigatória.");
        }

        List<Consulta> consultas =
                consultaRepository.findByMedicoEspecialidade(especialidade);

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("especialidade", especialidade);
        relatorio.put("totalConsultas", consultas.size());
        relatorio.put("consultas", consultas);

        return relatorio;
    }

    // =====================================================
    // RELATÓRIO DETALHADO COM FILTROS
    // =====================================================

    public Map<String, Object> gerarRelatorioDetalhado(
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            LocalTime horaInicio,
            LocalTime horaFim,
            String estado,
            Long medicoId,
            String especialidade) {

        validarPeriodo(dataInicio, dataFim);

        if (horaInicio != null && horaFim != null
                && horaInicio.isAfter(horaFim)) {

            throw new IllegalArgumentException(
                    "A hora de início não pode ser posterior à hora de fim.");
        }

        if (estado != null && !estado.isBlank()
                && !List.of("MARCADA", "CONCLUIDA", "CANCELADA")
                        .contains(estado.toUpperCase())) {

            throw new IllegalArgumentException(
                    "O estado da consulta indicado é inválido.");
        }

        if (medicoId != null && medicoId <= 0) {
            throw new IllegalArgumentException(
                    "O identificador do médico é inválido.");
        }

        List<Consulta> consultas =
                consultaRepository.findByDataHoraBetween(
                        dataInicio, dataFim);

        List<Consulta> filtradas = consultas.stream()

                .filter(c -> estado == null || estado.isBlank()
                        || estado.equalsIgnoreCase(c.getEstado()))

                .filter(c -> medicoId == null
                        || (c.getMedico() != null
                        && medicoId.equals(c.getMedico().getId())))

                .filter(c -> especialidade == null
                        || especialidade.isBlank()
                        || (c.getMedico() != null
                        && c.getMedico().getEspecialidade() != null
                        && especialidade.equalsIgnoreCase(
                                c.getMedico().getEspecialidade())))

                .filter(c -> horaInicio == null
                        || !c.getDataHora().toLocalTime()
                                .isBefore(horaInicio))

                .filter(c -> horaFim == null
                        || !c.getDataHora().toLocalTime()
                                .isAfter(horaFim))

                .toList();

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("dataInicio", dataInicio);
        relatorio.put("dataFim", dataFim);
        relatorio.put("horaInicio", horaInicio);
        relatorio.put("horaFim", horaFim);
        relatorio.put("estado", estado);
        relatorio.put("medicoId", medicoId);
        relatorio.put("especialidade", especialidade);
        relatorio.put("totalConsultas", filtradas.size());
        relatorio.put("consultas", filtradas);

        return relatorio;
    }

    // =====================================================
    // RELATÓRIO DO TOTAL DE PACIENTES
    // =====================================================

    public Map<String, Object> gerarRelatorioDePacientes() {

        long totalPacientes = pacienteRepository.count();

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("totalPacientes", totalPacientes);

        return relatorio;
    }

    // =====================================================
    // VALIDAR PERÍODO
    // =====================================================

    private void validarPeriodo(
            LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException(
                    "As datas de início e fim são obrigatórias.");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A data de início não pode ser posterior à data de fim.");
        }
    }

    // =====================================================
    // CALCULAR PERCENTAGEM
    // =====================================================

    private double calcularPercentagem(long quantidade, long total) {

        if (total == 0) {
            return 0.0;
        }

        return Math.round((quantidade * 10000.0) / total) / 100.0;
    }

    // =====================================================
    // RELATÓRIO DE NOVOS PACIENTES
    // =====================================================

    public Map<String, Object> gerarRelatorioNovosPacientes(
            LocalDate dataInicio,
            LocalDate dataFim) {

        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException(
                    "As datas de início e fim são obrigatórias.");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException(
                    "A data de início não pode ser posterior à data de fim.");
        }

        List<Paciente> pacientes =
                pacienteRepository.findByDataRegistoBetween(
                        dataInicio, dataFim);

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("dataInicio", dataInicio);
        relatorio.put("dataFim", dataFim);
        relatorio.put("totalNovosPacientes", pacientes.size());
        relatorio.put("pacientes", pacientes);

        return relatorio;
    }

    // =====================================================
    // DESEMPENHO MENSAL DA CLÍNICA
    // =====================================================

    public Map<String, Object> gerarDesempenhoMensal(int ano) {

        if (ano < 2000 || ano > 2100) {
            throw new IllegalArgumentException(
                    "O ano indicado é inválido.");
        }

        List<Map<String, Object>> desempenhoMensal =
                new ArrayList<>();

        long totalAnualConsultas = 0;
        long totalAnualPacientes = 0;

        for (int mes = 1; mes <= 12; mes++) {

            YearMonth periodo = YearMonth.of(ano, mes);

            LocalDate dataInicio = periodo.atDay(1);
            LocalDate dataFim = periodo.atEndOfMonth();

            LocalDateTime inicio = dataInicio.atStartOfDay();
            LocalDateTime fim = dataFim.atTime(23, 59, 59, 999999999);

            long totalConsultas =
                    consultaRepository.countByDataHoraBetween(
                            inicio, fim);

            long totalPacientes =
                    pacienteRepository.countByDataRegistoBetween(
                            dataInicio, dataFim);

            Map<String, Object> dadosMes = new LinkedHashMap<>();

            dadosMes.put("mes", mes);
            dadosMes.put("ano", ano);
            dadosMes.put("totalConsultas", totalConsultas);
            dadosMes.put("novosPacientes", totalPacientes);

            desempenhoMensal.add(dadosMes);

            totalAnualConsultas += totalConsultas;
            totalAnualPacientes += totalPacientes;
        }

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("ano", ano);
        relatorio.put("desempenhoMensal", desempenhoMensal);
        relatorio.put("totalAnualConsultas", totalAnualConsultas);
        relatorio.put("totalAnualPacientes", totalAnualPacientes);

        return relatorio;
    }

    // =====================================================
    // DASHBOARD GERAL DA CLÍNICA
    // =====================================================

    public Map<String, Object> gerarDashboard() {

        LocalDate hoje = LocalDate.now();

        LocalDateTime inicioDia = hoje.atStartOfDay();
        LocalDateTime fimDia = hoje.atTime(23, 59, 59, 999999999);

        LocalDateTime agora = LocalDateTime.now();

        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("totalPacientes", pacienteRepository.count());
        dashboard.put("totalMedicos", medicoRepository.count());
        dashboard.put("totalConsultas", consultaRepository.count());

        dashboard.put("consultasHoje",
                consultaRepository.countByDataHoraBetween(
                        inicioDia, fimDia));

        dashboard.put("listaConsultasHoje",
                consultaRepository.buscarConsultasDoDia(
                        inicioDia, fimDia));

        dashboard.put("proximasConsultas",
                consultaRepository.buscarProximasConsultas(agora)
                        .stream()
                        .limit(10)
                        .toList());

        dashboard.put("consultasPorEstado",
                gerarRelatorioPorEstado());

        dashboard.put("desempenhoMensal",
                gerarDesempenhoMensal(hoje.getYear()));

        return dashboard;
    }

    // =====================================================
    // RELATÓRIO DO MÉDICO AUTENTICADO
    // =====================================================

    public Map<String, Object> gerarRelatorioDoMedicoAutenticado() {

        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new IllegalStateException(
                    "Não existe um médico autenticado.");
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(autenticacao.getName());

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Utilizador autenticado não encontrado.");
        }

        Medico medico = medicoRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não foi encontrado um médico associado a esta conta."));

        List<Consulta> consultas =
                consultaRepository.findByMedicoId(medico.getId());

        Map<String, Object> relatorio = new LinkedHashMap<>();

        relatorio.put("medico", medico.getNome());
        relatorio.put("especialidade", medico.getEspecialidade());
        relatorio.put("totalConsultas", consultas.size());

        relatorio.put("marcadas", consultas.stream()
                .filter(c -> "MARCADA".equals(c.getEstado()))
                .count());

        relatorio.put("concluidas", consultas.stream()
                .filter(c -> "CONCLUIDA".equals(c.getEstado()))
                .count());

        relatorio.put("canceladas", consultas.stream()
                .filter(c -> "CANCELADA".equals(c.getEstado()))
                .count());

        relatorio.put("consultas", consultas);

        return relatorio;
    }

    // =====================================================
    // HISTÓRICO DO PACIENTE AUTENTICADO
    // =====================================================

    public Map<String, Object> gerarHistoricoDoPacienteAutenticado() {

        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new IllegalStateException(
                    "Não existe um paciente autenticado.");
        }

        Usuario usuario =
                usuarioService.buscarPorEmail(autenticacao.getName());

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Utilizador autenticado não encontrado.");
        }

        Paciente paciente = pacienteRepository
                .findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não foi encontrado um paciente associado a esta conta."));

        List<Consulta> consultas =
                consultaRepository.findByPacienteId(paciente.getId());

        Map<String, Object> historico = new LinkedHashMap<>();

        historico.put("paciente", paciente.getNome());
        historico.put("totalConsultas", consultas.size());

        historico.put("marcadas", consultas.stream()
                .filter(c -> "MARCADA".equals(c.getEstado()))
                .count());

        historico.put("concluidas", consultas.stream()
                .filter(c -> "CONCLUIDA".equals(c.getEstado()))
                .count());

        historico.put("canceladas", consultas.stream()
                .filter(c -> "CANCELADA".equals(c.getEstado()))
                .count());

        historico.put("consultas", consultas);

        return historico;
    }
}
