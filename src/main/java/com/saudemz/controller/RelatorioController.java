
package com.saudemz.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saudemz.service.RelatorioPdfService;
import com.saudemz.service.RelatorioService;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final RelatorioPdfService relatorioPdfService;

    public RelatorioController(
            RelatorioService relatorioService,
            RelatorioPdfService relatorioPdfService) {

        this.relatorioService = relatorioService;
        this.relatorioPdfService = relatorioPdfService;
    }

    // RELATÓRIO GERAL POR ESTADO
    @GetMapping("/consultas/estado")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioConsultasPorEstado() {
        return relatorioService.gerarRelatorioPorEstado();
    }

    // RELATÓRIO POR PERÍODO
    @GetMapping("/consultas/periodo")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioConsultasPorPeriodo(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim) {

        return relatorioService.gerarRelatorioPorPeriodo(
                dataInicio, dataFim);
    }

    // RELATÓRIO POR MÉDICO
    @GetMapping("/consultas/medico/{medicoId}")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioConsultasPorMedico(
            @PathVariable Long medicoId) {

        return relatorioService.gerarRelatorioPorMedico(medicoId);
    }

    // RELATÓRIO POR ESPECIALIDADE
    @GetMapping("/consultas/especialidade")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioConsultasPorEspecialidade(
            @RequestParam String especialidade) {

        return relatorioService.gerarRelatorioPorEspecialidade(especialidade);
    }

    // RELATÓRIO DETALHADO
    @GetMapping("/consultas/detalhado")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioDetalhado(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaFim,

            @RequestParam(required = false) String estado,

            @RequestParam(required = false) Long medicoId,

            @RequestParam(required = false) String especialidade) {

        return relatorioService.gerarRelatorioDetalhado(
                dataInicio,
                dataFim,
                horaInicio,
                horaFim,
                estado,
                medicoId,
                especialidade);
    }

    // EXPORTAR RELATÓRIO DETALHADO EM PDF
    @GetMapping("/consultas/detalhado/pdf")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public ResponseEntity<byte[]> exportarRelatorioPdf(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime horaFim,

            @RequestParam(required = false) String estado,

            @RequestParam(required = false) Long medicoId,

            @RequestParam(required = false) String especialidade) {

        Map<String, Object> relatorio =
                relatorioService.gerarRelatorioDetalhado(
                        dataInicio,
                        dataFim,
                        horaInicio,
                        horaFim,
                        estado,
                        medicoId,
                        especialidade);

        byte[] pdf =
                relatorioPdfService.gerarRelatorioConsultas(relatorio);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-consultas.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // RELATÓRIO DE NOVOS PACIENTES
    @GetMapping("/pacientes/periodo")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> relatorioNovosPacientes(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim) {

        return relatorioService.gerarRelatorioNovosPacientes(
                dataInicio, dataFim);
    }

    // DESEMPENHO MENSAL
    @GetMapping("/desempenho-mensal")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> desempenhoMensal(
            @RequestParam int ano) {

        return relatorioService.gerarDesempenhoMensal(ano);
    }

    // DASHBOARD GERAL
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Map<String, Object> dashboard() {
        return relatorioService.gerarDashboard();
    }

    // RELATÓRIO DO MÉDICO AUTENTICADO
    @GetMapping("/medico/meu-relatorio")
    @PreAuthorize("hasRole('MEDICO')")
    public Map<String, Object> relatorioDoMedico() {
        return relatorioService.gerarRelatorioDoMedicoAutenticado();
    }

    // HISTÓRICO DO PACIENTE AUTENTICADO
    @GetMapping("/paciente/meu-historico")
    @PreAuthorize("hasRole('PACIENTE')")
    public Map<String, Object> historicoDoPaciente() {
        return relatorioService.gerarHistoricoDoPacienteAutenticado();
    }
}
