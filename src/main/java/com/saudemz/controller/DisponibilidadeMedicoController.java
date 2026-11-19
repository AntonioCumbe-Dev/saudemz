package com.saudemz.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.saudemz.model.DisponibilidadeMedico;
import com.saudemz.service.ConsultaService;
import com.saudemz.service.DisponibilidadeMedicoService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeMedicoController {

    private final DisponibilidadeMedicoService disponibilidadeService;
    private final ConsultaService consultaService;

    public DisponibilidadeMedicoController(
            DisponibilidadeMedicoService disponibilidadeService,
            ConsultaService consultaService) {

        this.disponibilidadeService = disponibilidadeService;
        this.consultaService = consultaService;
    }

    // REGISTAR HORÁRIO DE UM MÉDICO
    @PostMapping("/medico/{medicoId}")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public ResponseEntity<DisponibilidadeMedico> registar(
            @PathVariable Long medicoId,
            @RequestBody DisponibilidadeMedico disponibilidade) {

        DisponibilidadeMedico nova =
                disponibilidadeService.registar(medicoId, disponibilidade);

        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    // CONSULTAR HORÁRIOS DE UM MÉDICO
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<DisponibilidadeMedico>> listarPorMedico(
            @PathVariable Long medicoId) {

        return ResponseEntity.ok(
                disponibilidadeService.listarPorMedico(medicoId));
    }

    // CONSULTAR MÉDICOS DISPONÍVEIS NUM DIA
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<DisponibilidadeMedico>> listarPorDia(
            @PathVariable DayOfWeek dia) {

        return ResponseEntity.ok(
                disponibilidadeService.listarPorDia(dia));
    }

    // CONSULTAR APENAS OS HORÁRIOS OCUPADOS DE UM MÉDICO NUMA DATA
    @GetMapping("/medico/{medicoId}/ocupados")
    public ResponseEntity<List<LocalTime>> listarHorariosOcupados(
            @PathVariable Long medicoId,
            @RequestParam LocalDate data) {

        List<LocalTime> horarios =
                consultaService.listarHorariosOcupados(medicoId, data);

        return ResponseEntity.ok(horarios);
    }

    // DESACTIVAR HORÁRIO
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public ResponseEntity<DisponibilidadeMedico> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                disponibilidadeService.desactivar(id));
    }
}