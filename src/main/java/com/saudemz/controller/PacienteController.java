
package com.saudemz.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.saudemz.dto.RegistoPacienteRequest;
import com.saudemz.model.Paciente;
import com.saudemz.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // CADASTRO PÚBLICO DE PACIENTE
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente cadastrarPaciente(
            @RequestBody RegistoPacienteRequest dados) {

        return pacienteService.registarPaciente(dados);
    }

    // REGISTAR PACIENTE PELO SUPERUTILIZADOR
    @PostMapping
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente registarPaciente(
            @RequestBody RegistoPacienteRequest dados) {

        return pacienteService.registarPaciente(dados);
    }

    // LISTAR TODOS OS PACIENTES
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO')")
    public List<Paciente> listarPacientes() {

        return pacienteService.listarPacientes();
    }

    // BUSCAR PACIENTE PELO ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO')")
    public Paciente buscarPacientePorId(@PathVariable Long id) {

        return pacienteService.buscarPacientePorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente não encontrado"));
    }

    // ACTUALIZAR PACIENTE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public Paciente actualizarPaciente(
            @PathVariable Long id,
            @RequestBody Paciente dadosActualizados) {

        return pacienteService.actualizarPaciente(
                id, dadosActualizados);
    }

    // ELIMINAR PACIENTE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public void eliminarPaciente(@PathVariable Long id) {

        pacienteService.eliminarPaciente(id);
    }
}
