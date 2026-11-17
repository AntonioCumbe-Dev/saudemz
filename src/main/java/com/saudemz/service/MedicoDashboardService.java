
package com.saudemz.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saudemz.model.Consulta;

@Service
@Transactional(readOnly = true)
public class MedicoDashboardService {

    private final ConsultaService consultaService;

    public MedicoDashboardService(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    // DASHBOARD DO MÉDICO AUTENTICADO
    public Map<String, Object> gerarDashboard() {

        LocalDate hoje = LocalDate.now();

        // CONSULTAS DO MÉDICO AUTENTICADO
        List<Consulta> consultas = consultaService.listarTodas();

        // TOTAL DE CONSULTAS POR ESTADO
        long totalMarcadas = consultas.stream()
                .filter(c -> "MARCADA".equals(c.getEstado()))
                .count();

        long totalConcluidas = consultas.stream()
                .filter(c -> "CONCLUIDA".equals(c.getEstado()))
                .count();

        long totalCanceladas = consultas.stream()
                .filter(c -> "CANCELADA".equals(c.getEstado()))
                .count();

        // CONSULTAS DE HOJE, EXCLUINDO AS CANCELADAS
        List<Consulta> consultasHoje = consultas.stream()
                .filter(c -> c.getDataHora().toLocalDate().equals(hoje))
                .filter(c -> !"CANCELADA".equals(c.getEstado()))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .toList();

        // PRÓXIMAS 10 CONSULTAS A PARTIR DE AMANHÃ
        List<Consulta> proximasConsultas = consultas.stream()
                .filter(c -> "MARCADA".equals(c.getEstado()))
                .filter(c -> c.getDataHora().toLocalDate().isAfter(hoje))
                .sorted(Comparator.comparing(Consulta::getDataHora))
                .limit(10)
                .toList();

        // HISTÓRICO DAS CONSULTAS CONCLUÍDAS E CANCELADAS
        List<Consulta> historicoConsultas = consultas.stream()
                .filter(c -> "CONCLUIDA".equals(c.getEstado())
                        || "CANCELADA".equals(c.getEstado()))
                .sorted(Comparator.comparing(Consulta::getDataHora).reversed())
                .toList();

        // CONSTRUIR DADOS DO DASHBOARD
        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("totalConsultas", consultas.size());
        dashboard.put("consultasHoje", consultasHoje.size());
        dashboard.put("totalMarcadas", totalMarcadas);
        dashboard.put("totalConcluidas", totalConcluidas);
        dashboard.put("totalCanceladas", totalCanceladas);

        dashboard.put("listaConsultasHoje", consultasHoje);
        dashboard.put("proximasConsultas", proximasConsultas);
        dashboard.put("historicoConsultas", historicoConsultas);

        return dashboard;
    }
}
