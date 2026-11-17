package com.saudemz.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saudemz.model.DisponibilidadeMedico;

@Repository
public interface DisponibilidadeMedicoRepository
        extends JpaRepository<DisponibilidadeMedico, Long> {

    List<DisponibilidadeMedico> findByMedicoId(Long medicoId);

    List<DisponibilidadeMedico> findByMedicoIdAndDiaSemanaAndDisponivelTrue(
            Long medicoId, DayOfWeek diaSemana);

    List<DisponibilidadeMedico> findByDiaSemanaAndDisponivelTrue(
            DayOfWeek diaSemana);
}