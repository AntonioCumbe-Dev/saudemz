
package com.saudemz.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saudemz.model.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // CONFLITOS NA AGENDA DO MÉDICO
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.medico.id = :medicoId
              AND c.dataHora < :fim
              AND c.dataHora > :inicioMenos30
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            """)
    List<Consulta> buscarConflitosDeHorario(
            @Param("medicoId") Long medicoId,
            @Param("inicioMenos30") LocalDateTime inicioMenos30,
            @Param("fim") LocalDateTime fim);

    // CONFLITOS NA AGENDA DO PACIENTE
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.paciente.id = :pacienteId
              AND c.dataHora < :fim
              AND c.dataHora > :inicioMenos30
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            """)
    List<Consulta> buscarConflitosDoPaciente(
            @Param("pacienteId") Long pacienteId,
            @Param("inicioMenos30") LocalDateTime inicioMenos30,
            @Param("fim") LocalDateTime fim);

    // CONFLITOS DO MÉDICO, EXCLUINDO A CONSULTA ACTUAL
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.medico.id = :medicoId
              AND c.id <> :consultaId
              AND c.dataHora < :fim
              AND c.dataHora > :inicioMenos30
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            """)
    List<Consulta> buscarConflitosDeHorarioExcluindoConsulta(
            @Param("medicoId") Long medicoId,
            @Param("consultaId") Long consultaId,
            @Param("inicioMenos30") LocalDateTime inicioMenos30,
            @Param("fim") LocalDateTime fim);

    // CONFLITOS DO PACIENTE, EXCLUINDO A CONSULTA ACTUAL
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.paciente.id = :pacienteId
              AND c.id <> :consultaId
              AND c.dataHora < :fim
              AND c.dataHora > :inicioMenos30
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            """)
    List<Consulta> buscarConflitosDoPacienteExcluindoConsulta(
            @Param("pacienteId") Long pacienteId,
            @Param("consultaId") Long consultaId,
            @Param("inicioMenos30") LocalDateTime inicioMenos30,
            @Param("fim") LocalDateTime fim);

    // CONSULTAS DE UM MÉDICO
    List<Consulta> findByMedicoId(Long medicoId);

    Optional<Consulta> findByIdAndMedicoId(Long id, Long medicoId);

    // CONSULTAS DE UM PACIENTE
    List<Consulta> findByPacienteId(Long pacienteId);

    Optional<Consulta> findByIdAndPacienteId(Long id, Long pacienteId);

    // HORÁRIOS OCUPADOS DE UM MÉDICO NUMA DATA
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.medico.id = :medicoId
              AND c.dataHora >= :inicio
              AND c.dataHora < :fim
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            ORDER BY c.dataHora ASC
            """)
    List<Consulta> buscarHorariosOcupados(
            @Param("medicoId") Long medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // CONSULTAS PARA LEMBRETE
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.estado = 'MARCADA'
              AND c.lembreteEnviado = false
              AND c.dataHora BETWEEN :inicio AND :fim
            """)
    List<Consulta> buscarConsultasParaLembrete(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // PESQUISAR PELO CÓDIGO
    Optional<Consulta> findByCodigoConsulta(String codigoConsulta);

    boolean existsByCodigoConsulta(String codigoConsulta);

    // CONSULTAS DO DIA
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.dataHora BETWEEN :inicio AND :fim
              AND (c.estado IS NULL OR c.estado <> 'CANCELADA')
            ORDER BY c.dataHora ASC
            """)
    List<Consulta> buscarConsultasDoDia(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // PRÓXIMAS CONSULTAS MARCADAS
    @Query("""
            SELECT c FROM Consulta c
            WHERE c.estado = 'MARCADA'
              AND c.dataHora >= :agora
            ORDER BY c.dataHora ASC
            """)
    List<Consulta> buscarProximasConsultas(
            @Param("agora") LocalDateTime agora);

    // RELATÓRIOS
    long countByEstado(String estado);

    long countByDataHoraBetween(
            LocalDateTime dataInicio,
            LocalDateTime dataFim);

    List<Consulta> findByDataHoraBetween(
            LocalDateTime dataInicio,
            LocalDateTime dataFim);

    long countByMedicoId(Long medicoId);

    long countByMedicoEspecialidade(String especialidade);

    List<Consulta> findByMedicoEspecialidade(String especialidade);
}
