package com.saudemz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saudemz.model.SequenciaConsulta;

public interface SequenciaConsultaRepository
        extends JpaRepository<SequenciaConsulta, String> {

    @Query(value = """
            SELECT *
            FROM sequencia_consulta
            WHERE data_codigo = :dataCodigo
            FOR UPDATE
            """, nativeQuery = true)
    Optional<SequenciaConsulta> buscarPorDataComBloqueio(
            @Param("dataCodigo") String dataCodigo);
}