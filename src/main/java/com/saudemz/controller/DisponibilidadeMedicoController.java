package com.saudemz.controller;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.saudemz.model.DisponibilidadeMedico;
import com.saudemz.service.DisponibilidadeMedicoService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeMedicoController {

    private final DisponibilidadeMedicoService disponibilidadeService;

    public DisponibilidadeMedicoController(
            DisponibilidadeMedicoService disponibilidadeService) {
        this.disponibilidadeService = disponibilidadeService;
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

    // DESACTIVAR HORÁRIO
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public ResponseEntity<DisponibilidadeMedico> desactivar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                disponibilidadeService.desactivar(id));
    }
}