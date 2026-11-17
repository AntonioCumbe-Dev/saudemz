
package com.saudemz.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saudemz.dto.RegistoMedicoRequest;
import com.saudemz.model.Medico;
import com.saudemz.service.MedicoService;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    // Registar médico e criar conta automaticamente
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    @PostMapping
    public Medico registar(@RequestBody RegistoMedicoRequest dados) {

        Medico medico = new Medico(
                dados.getNome(),
                dados.getEspecialidade(),
                dados.getTelefone(),
                dados.getEmail()
        );

        return medicoService.salvarDados(medico, dados.getSenha());
    }

    // Listar médicos
    @GetMapping
    public List<Medico> listarTodos() {
        return medicoService.listarTodos();
    }

    // Buscar médico pelo ID
    @GetMapping("/{id}")
    public Medico buscarPorId(@PathVariable Long id) {
        return medicoService.buscarPorId(id);
    }

    // Actualizar médico
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    @PutMapping("/{id}")
    public Medico actualizar(
            @PathVariable Long id,
            @RequestBody Medico dados) {
        return medicoService.actualizar(id, dados);
    }

    // Eliminar médico
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        medicoService.eliminar(id);
    }
}
