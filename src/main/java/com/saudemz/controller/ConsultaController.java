
package com.saudemz.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.saudemz.model.Consulta;
import com.saudemz.service.ConsultaService;
import com.saudemz.service.ReciboPdfService;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ReciboPdfService reciboPdfService;

    public ConsultaController(
            ConsultaService consultaService,
            ReciboPdfService reciboPdfService) {

        this.consultaService = consultaService;
        this.reciboPdfService = reciboPdfService;
    }

    // MARCAR CONSULTA PELO SUPERUTILIZADOR
    @PostMapping
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public ResponseEntity<Consulta> marcarConsulta(
            @RequestBody Consulta consulta) {

        Consulta novaConsulta =
                consultaService.marcarConsulta(consulta);

        return ResponseEntity.status(201).body(novaConsulta);
    }

    // MARCAR CONSULTA PELO PACIENTE
    @PostMapping("/minhas")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Consulta> marcarConsultaPaciente(
            @RequestBody Consulta consulta) {

        Consulta novaConsulta =
                consultaService.marcarConsultaPaciente(consulta);

        return ResponseEntity.status(201).body(novaConsulta);
    }

    // LISTAR CONSULTAS CONFORME O PERFIL
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO', 'PACIENTE')")
    public List<Consulta> listarTodas() {

        return consultaService.listarTodas();
    }

    // LISTAR CONSULTAS DO PACIENTE AUTENTICADO
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<Consulta> listarMinhasConsultas() {

        return consultaService.listarTodas();
    }

    // CONSULTAS DE HOJE - DASHBOARD ADMINISTRATIVO
    @GetMapping("/hoje")
    @PreAuthorize("hasRole('SUPERUTILIZADOR')")
    public List<Consulta> listarConsultasDoDia() {

        return consultaService.listarConsultasDoDia();
    }

    // PROCURAR CONSULTA POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO', 'PACIENTE')")
    public Consulta buscarPorId(@PathVariable Long id) {

        return consultaService.buscarPorId(id);
    }

    // CONSULTAR CONSULTA PELO CÓDIGO ÚNICO
    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO', 'PACIENTE')")
    public Consulta buscarPorCodigo(
            @PathVariable String codigo) {

        return consultaService.buscarPorCodigoConsulta(codigo);
    }

    // DESCARREGAR RECIBO PDF
    @GetMapping("/{id}/recibo")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO', 'PACIENTE')")
    public ResponseEntity<byte[]> descarregarRecibo(
            @PathVariable Long id) {

        Consulta consulta = consultaService.buscarPorId(id);

        byte[] pdf = reciboPdfService.gerarRecibo(consulta);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=recibo-saudemz-"
                                + id + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    // REAGENDAR CONSULTA
    @PatchMapping("/{id}/reagendar")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'PACIENTE')")
    public Consulta reagendarConsulta(
            @PathVariable Long id,
            @RequestBody Consulta novosDados) {

        return consultaService.reagendarConsulta(id, novosDados);
    }

    // ACTUALIZAR ESTADO DA CONSULTA
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('SUPERUTILIZADOR', 'MEDICO', 'PACIENTE')")
    public Consulta actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> dados) {

        return consultaService.actualizarEstado(
                id, dados.get("estado"));
    }
}
