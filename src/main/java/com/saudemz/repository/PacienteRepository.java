package com.saudemz.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saudemz.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByUsuarioId(Long usuarioId);
    
 // RELATÓRIOS: CONTAR PACIENTES REGISTADOS POR PERÍODO
    long countByDataRegistoBetween(
            LocalDate dataInicio,
            LocalDate dataFim);

    // RELATÓRIOS: LISTAR PACIENTES REGISTADOS POR PERÍODO
    List<Paciente> findByDataRegistoBetween(
            LocalDate dataInicio,
            LocalDate dataFim);

}